package ru.javaops.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaops.ApplicationAbstractTest;
import ru.javaops.model.ParticipationType;
import ru.javaops.model.User;
import ru.javaops.to.UserMail;

/**
 * GKislin
 * 16.02.2016
 */
public class MailServiceTest extends ApplicationAbstractTest {

    @Autowired
    private MailService mailService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    public void testSendMail() throws Exception {
        System.out.println(mailService.sendTest("jr_confirm"));
    }

    @Test
    public void testTemplate() throws Exception {
        User userMail = mailService.getAppUser();
        String activationKey = subscriptionService.generateActivationKey(userMail.getEmail());
        String subscriptionUrl = subscriptionService.getSubscriptionUrl(userMail.getEmail(), activationKey, false);

        String content = mailService.getContent("jr_confirm",
                ImmutableMap.of("participationType", ParticipationType.HW_REVIEW,
                        "user", new UserMail(userMail),
                        "subscriptionUrl", subscriptionUrl,
                        "activationKey", activationKey));

        System.out.println(content);
    }
}