package ru.javaops.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaops.ApplicationAbstractTest;

import javax.crypto.spec.SecretKeySpec;

/**
 * GKislin
 * 25.02.2016
 */
public class SubscriptionServiceTest  extends ApplicationAbstractTest {

    public static final String EMAIL = "dummy@yandex.ru";

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    public void testActivationKey() throws Exception {
        String key = subscriptionService.generateActivationKey(EMAIL);
        System.out.println("+++++++++");
        System.out.println(key);
        System.out.println("+++++++++");
        subscriptionService.checkActivationKey(EMAIL, key);
    }

    @Test
    public void encryptDecrypt() throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec("vGHR#%&^$&@GHDGd".getBytes(), "AES");
        String mail = "full.name@mail.com.ru";
        String code = SubscriptionService.encrypt0(mail, secretKey);
        System.out.println(code);
        String recoveredMail = SubscriptionService.decrypt0(code, secretKey);
        System.out.println(mail);
        Assert.assertEquals(mail, recoveredMail);
    }
}