package ru.javaops.util;

import org.apache.commons.lang3.text.WordUtils;
import org.junit.Test;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.junit.Assert.assertTrue;

/**
 * GKislin
 * 11.02.2016
 */
public class PasswordUtilTest {

    @Test
    public void test() throws Exception {
        System.out.println(substringBefore(capitalize("григорий Кислин"), " "));
        System.out.println(WordUtils.capitalize("Санкт,петербург", ' ', '-', '.',','));
    }

    @Test
    public void testPassword() throws Exception {
        String rawPassword = "password";
        String encoded = PasswordUtil.encode(rawPassword);
        System.out.println(encoded);
        assertTrue(PasswordUtil.isEncoded(encoded));
        assertTrue(PasswordUtil.isMatch(rawPassword, encoded));
    }
}