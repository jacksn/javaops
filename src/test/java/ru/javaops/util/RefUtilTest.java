package ru.javaops.util;

import org.junit.Assert;
import org.junit.Test;

import javax.crypto.spec.SecretKeySpec;

/**
 * GKislin
 * 25.02.2016
 */
public class RefUtilTest {

    @Test
//    $vZDS4I86C3mPhvvo_lXKTQDCBJK7AdUl4nTm8Zso5RQ=
    public void encryptDecrypt() throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec("".getBytes(), "AES");
        String mail = "admin@javaops.ru";
        String code = RefUtil.encrypt0(mail, secretKey);
        System.out.println(code);
        String recoveredMail = RefUtil.decrypt0(code, secretKey);
        System.out.println(mail);
        Assert.assertEquals(mail, recoveredMail);
    }
}