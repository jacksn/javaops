package ru.javaops.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mail.MailProperties;
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
public class MailService {
    private static final Locale LOCALE_RU = Locale.forLanguageTag("ru");
    private static final String OK = "OK";
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private MailCaseRepository mailCaseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    @Qualifier("mailExecutor")
    private Executor mailExecutor;

    public static boolean isOk(String result) {
        return OK.equals(result);
    }

    public GroupResult sendToEmailList(String template, Collection<String> emails) {
        return sendToUserList(template, emails.stream().map(userService::findExistedByEmail).collect(Collectors.toSet()));
    }

    public GroupResult sendToUserList(String template, Collection<User> users) {
        checkNotNull(template, "template must not be null");
        checkNotNull(users, "users must not be null");
        users.add(getTestUser());
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

    private User getTestUser() {
        return userService.findByEmail(appProperties.getTestEmail());
    }

    private void cancelAll(Map<Future<String>, String> resultMap) {
        LOG.warn("Cancel all unsent tasks");
        resultMap.forEach((feature, email) -> {
            LOG.warn("Sending to " + email + " failed");
            feature.cancel(true);
        });
    }

    @Async("mailExecutor")
    public Future<String> sendToUserAsync(String template, String email) {
        return new AsyncResult<>(sendToUser(template, email));
    }

    public String sendTest(String template) {
        return sendToUser(template, getTestUser());
    }

    public String sendToUser(String template, String email) {
        checkNotNull(template, "Template must not be null");
        checkNotNull(email, "Email must not be null");
        return sendToUser(template, userService.findByEmail(email));
    }

    public String sendToUser(String template, User user) {
        checkNotNull(user, "User must not be null");
        String activationKey = subscriptionService.generateActivationKey(user.getEmail());
        String subscriptionUrl = subscriptionService.getSubscriptionUrl(user.getEmail(), activationKey, false);
        return sendToUserWithParams(template, user, ImmutableMap.of("user", user, "subscriptionUrl", subscriptionUrl, "activationKey", activationKey));
    }

    public String sendToUserWithParams(String template, User user, final Map<String, ?> params) {
        checkNotNull(user, "User must not be null");
        LOG.debug("Sending {} email to '{}'", template, user.getEmail());
        String content = getContent(template, params);
        final String subject = Util.getTitle(content);
        String result;
        try {
            send(user.getEmail(), user.getFirstName(), subject, content, true, (String) params.get("subscriptionUrl"));
            result = OK;
        } catch (MessagingException | MailException e) {
            result = e.getMessage();
            LOG.error("Sending to {} failed: \n{}", user.getEmail(), result);
        }
        mailCaseRepository.save(new MailCase(user, template, result));
        return result;
    }

    public void send(String toEmail, String toName, String subject, String content, boolean isHtml, String subscriptionUrl) throws MessagingException {
        LOG.debug("Send email to '{} <{}>' with subject '{}'", toName, toEmail, subject);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        if (subscriptionUrl != null) {
            mimeMessage.setHeader("List-Unsubscribe", '<' + subscriptionUrl + '>');
        }
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        try {
            message.setTo(new InternetAddress(toEmail, toName, "UTF-8"));
            message.setFrom(mailProperties.getUsername(), "Java Online Projects");
        } catch (UnsupportedEncodingException e) { // dummy
        }
        message.setSubject(subject);
        message.setText(content, isHtml);
        javaMailSender.send(mimeMessage);
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
                LOG.error("Sending to " + email + " interrupted");
            } catch (ExecutionException e) {
                failed.add(new MailResult(email, e.toString()));
                LOG.error("Sending to " + email + " failed with " + e.getMessage());
            }
            return (failed.size() < success || failed.size() < 3) && failedCause == null;
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
