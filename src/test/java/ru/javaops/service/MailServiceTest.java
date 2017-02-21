package ru.javaops.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaops.ApplicationAbstractTest;

/**
 * GKislin
 * 16.02.2016
 */
public class MailServiceTest extends ApplicationAbstractTest {

    @Autowired
    private MailService mailService;

    @Test
    public void testSendMail() throws Exception {
        System.out.println(mailService.sendTest("jr_confirm"));
    }
}