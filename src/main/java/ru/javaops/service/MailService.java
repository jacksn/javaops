package ru.javaops.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import ru.javaops.config.AppProperties;
import ru.javaops.model.MailCase;
import ru.javaops.model.User;
import ru.javaops.repository.MailCaseRepository;
import ru.javaops.to.UserMail;
import ru.javaops.util.Util;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Slf4j
public class MailService {
    private static final Locale LOCALE_RU = Locale.forLanguageTag("ru");
    public static final String OK = "OK";

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailCaseRepository mailCaseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private RefService refService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    @Qualifier("mailExecutor")
    private Executor mailExecutor;

    public static boolean isOk(String result) {
        return OK.equals(result);
    }

    public GroupResult sendToEmailList(String template, Collection<String> emails) {
        return sendToUserList(template, emails.stream().map(email -> userService.findExistedByEmail(email)).collect(Collectors.toSet()));
    }

    public GroupResult sendToUserList(String template, Set<UserMail> users) {
        checkNotNull(template, " template must not be null");
        checkNotNull(users, " users must not be null");
        users.add(getAppUser());
        CompletionService<String> completionService = new ExecutorCompletionService<>(mailExecutor);
        Map<Future<String>, String> resultMap = new HashMap<>();
        users.forEach(
                u -> {
                    Future<String> future = completionService.submit(() -> sendToUser(template, u));
                    resultMap.put(future, u.getEmail());
                }
        );

        final GroupResultBuilder groupResultBuilder = new GroupResultBuilder();
        try {
            while (!resultMap.isEmpty()) {
                Future<String> future = completionService.poll(10, TimeUnit.SECONDS);
                if (future == null) {
                    cancelAll(resultMap);
                    return groupResultBuilder.buildWithFailure("+++ Interrupted by timeout");
                } else {
                    String email = resultMap.remove(future);
                    if (!groupResultBuilder.accept(email, future)) {
                        cancelAll(resultMap);
                        return groupResultBuilder.buildWithFailure("+++ Interrupted by faults number");
                    }
                }
            }
        } catch (InterruptedException e) {
            return groupResultBuilder.buildWithFailure("+++ Interrupted");
        }
        return groupResultBuilder.buildOK();
    }

    User getAppUser() {
        return userService.findExistedByEmail(appProperties.getEmail());
    }

    private void cancelAll(Map<Future<String>, String> resultMap) {
        log.warn("Cancel all unsent tasks");
        resultMap.forEach((feature, email) -> {
            log.warn("Sending to " + email + " failed");
            feature.cancel(true);
        });
    }

    @Async("mailExecutor")
    public Future<String> sendWithTemplateAsync(UserMail userMail, String template, final Map<String, ?> params) {
        return new AsyncResult<>(sendWithTemplate(template, userMail, params));
    }

    public String sendTest(String template) {
        return sendToUser(template, getAppUser());
    }

    public String sendToUser(String template, String email) {
        checkNotNull(email, "Email must not be null");
        return sendToUser(template, userService.findExistedByEmail(email));
    }

    public String sendToUser(String template, UserMail userMail) {
        return sendWithTemplate(template, userMail, ImmutableMap.of());
    }

    public void sendRefMail(User refUser, String template, Map<String, ?> params) {
        sendWithTemplateAsync(refUser, template, params);
    }

    public String sendWithTemplate(String template, UserMail userMail, final Map<String, ?> params) {
        checkNotNull(template, "Template must not be null");
        checkNotNull(userMail, "User must not be null");
        final String email = checkNotNull(userMail.getEmail());

        log.debug("Sending {} email to '{}'", template, email);
        String activationKey = subscriptionService.generateActivationKey(email);
        String subscriptionUrl = subscriptionService.getSubscriptionUrl(email, activationKey, false);
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                .putAll(params)
                .put("user", userMail)
                .put("subscriptionUrl", subscriptionUrl)
                .put("activationKey", activationKey);

        if (template.startsWith("ref/")) {
            builder.put("topjavaRef", refService.getRefUrl("topjava", email))
                    .put("masterjavaRef", refService.getRefUrl("masterjava", email))
                    .put("basejavaRef", refService.getRefUrl("basejava", email));
        }

        String content = getContent(template, builder.build());
        final String subject = Util.getTitle(content);
        String result;
        try {
            send(email, userMail.getFullName(), subject, content, true, (String) params.get("subscriptionUrl"));
            result = OK;
        } catch (MessagingException | MailException e) {
            result = e.getMessage();
            log.error("Sending to {} failed: \n{}", email, result);
            mailCaseRepository.save(new MailCase(userMail, template, result));
        }
        return result;
    }

    public void send(String toEmail, String toName, String subject, String content, boolean isHtml, String subscriptionUrl) throws MessagingException {
        log.debug("Send email to '{} <{}>' with subject '{}'", toName, toEmail, subject);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        if (subscriptionUrl != null) {
            mimeMessage.setHeader("List-Unsubscribe", '<' + subscriptionUrl + '>');
        }
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        try {
            message.setTo(new InternetAddress(toEmail, toName, "UTF-8"));
            message.setFrom(appProperties.getEmail(), "Java Online Projects");
        } catch (UnsupportedEncodingException e) { // dummy
        }
        message.setSubject(subject);
        message.setText(content, isHtml);
        javaMailSender.send(mimeMessage);
    }

    public GroupResult resendTodayFailed(String template) {
        List<MailCase> todayFailed = mailCaseRepository.getTodayFailed();
        return sendToUserList(template, todayFailed.stream().map(MailCase::getUserMail).collect(Collectors.toSet()));
    }

    public static class GroupResultBuilder {
        private int success = 0;
        private List<MailResult> failed = new ArrayList<>();
        private String failedCause = null;

        private GroupResult buildOK() {
            return new GroupResult(success, failed, null);
        }

        private GroupResult buildWithFailure(String cause) {
            return new GroupResult(success, failed, cause);
        }

        private boolean accept(String email, Future<String> future) {
            try {
                final String result = future.get();
                if (isOk(result)) {
                    success++;
                } else {
                    failed.add(new MailResult(email, result));
                }
            } catch (InterruptedException e) {
                failedCause = "Task interrupted";
                log.error("Sending to " + email + " interrupted");
            } catch (ExecutionException e) {
                failed.add(new MailResult(email, e.toString()));
                log.error("Sending to " + email + " failed with " + e.getMessage());
            }
            double failedSize = failed.size();
            return (failedSize < 6. || failedSize / (success + failedSize) < 0.15) && failedCause == null;
        }
    }

    String getContent(String template, final Map<String, ?> params) {
        Context context = new Context(LOCALE_RU, params);
        return templateEngine.process(template, context);
    }

    public static class MailResult {
        private final String email;
        private final String result;

        public MailResult(@JsonProperty("email") String email, @JsonProperty("result") String result) {
            this.email = email;
            this.result = result;
        }

        @Override
        public String toString() {
            return '(' + email + ',' + result + ')';
        }
    }

    public static class GroupResult {
        private final int success;
        private final List<MailResult> failed;
        private final String failedCause;

        public GroupResult(@JsonProperty("success") int success, @JsonProperty("failed") List<MailResult> failed, @JsonProperty("failedCause") String failedCause) {
            this.success = success;
            this.failed = ImmutableList.copyOf(failed);
            this.failedCause = failedCause;
        }

        @Override
        public String toString() {
            return "Success: " + success + '\n' +
                    "Failed: " + failed.toString() + '\n' +
                    (failedCause == null ? "" : "Failed cause" + failedCause);
        }

        public boolean isOk() {
            return failedCause == null;
        }
    }
}
