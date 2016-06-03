package ru.javaops.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * gkislin
 * 03.06.2016
 */
public class UserUtilTest {
    @Test
    public void checkGmailExp() throws Exception {
        Assert.assertTrue(UserUtil.GMAIL_EXP.matcher("werw@gmail.ru").find());
        Assert.assertFalse(UserUtil.GMAIL_EXP.matcher("werwgmail.ru").find());
        Assert.assertFalse(UserUtil.GMAIL_EXP.matcher("werw@gmailru").find());
    }
}