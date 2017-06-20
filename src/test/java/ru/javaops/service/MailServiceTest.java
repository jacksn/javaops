package ru.javaops.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaops.ApplicationAbstractTest;
import ru.javaops.model.User;

/**
 * GKislin
 * 16.02.2016
 */
public class MailServiceTest extends ApplicationAbstractTest {

    @Autowired
    private MailService mailService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private RefService refService;

    @Test
    public void testSendMail() throws Exception {
        System.out.println(mailService.sendTest("jr_confirm"));
    }

    @Test
    public void testTemplate() throws Exception {
        User user = mailService.getAppUser();
        String activationKey = subscriptionService.generateActivationKey(user.getEmail());
        String subscriptionUrl = subscriptionService.getSubscriptionUrl(user.getEmail(), activationKey, false);

        String email = user.getEmail();
        String content = mailService.getContent("ref/refParticipation",
                ImmutableMap.of(
                        "user", user,
                        "topjavaRef", refService.getRefUrl("topjava", email),
                        "masterjavaRef", refService.getRefUrl("masterjava", email),
                        "basejavaRef", refService.getRefUrl("basejava", email)));

        System.out.println(content);
    }
}