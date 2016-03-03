package ru.javaops.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaops.ApplicationAbstractTest;

/**
 * GKislin
 * 05.02.2016
 */
public class JacksonTest extends ApplicationAbstractTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetResult() throws Exception {
        MailService.GroupResult res = new MailService.GroupResult(2, ImmutableList.of(new MailService.MailResult("c@ya.ru", "fail_c"), new MailService.MailResult("d@ya.ru", "fail_d")), null);
        System.out.println(res);
        String str = objectMapper.writeValueAsString(res);
        System.out.println(str);
        Assert.assertEquals(res.toString(), objectMapper.readerFor(MailService.GroupResult.class).readValue(str).toString());
    }
}