package ru.javaops.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaops.ApplicationAbstractTest;
import ru.javaops.UserTestData;
import ru.javaops.util.Util;

/**
 * GKislin
 * 16.02.2016
 */
public class MailServiceTest extends ApplicationAbstractTest {

    @Autowired
    private MailService mailService;

    @Test
    public void testGetContent() throws Exception {
        String content = mailService.getContent("basejava_register",
                ImmutableMap.of("user", UserTestData.USER, "template", "topjava_register", "participation", "REGISTRATION", "result", "OK", "activationKey", "activationKey"));
        System.out.println("\n+++++");
        System.out.println(Util.getTitle(content));
        System.out.println("+++++");
        System.out.println(content);
    }
}