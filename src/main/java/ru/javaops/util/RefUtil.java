package ru.javaops.util;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * gkislin
 * 19.06.2017
 */
@Slf4j
public class RefUtil {
    public static String encrypt0(String value, SecretKey secretKey) {
        try {
            byte[] bytes = value.getBytes();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedByte = cipher.doFinal(bytes);
            return Base64.getUrlEncoder().encodeToString(encryptedByte);
        } catch (Exception e) {
            log.warn("!!! Error encrypt '{}'", value);
            return null;
        }
    }

    public static String decrypt0(String encrypted, SecretKey secretKey) {
        try {
            byte[] encryptedByte = Base64.getUrlDecoder().decode(encrypted);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(encryptedByte);
            return new String(bytes);
        } catch (Exception e) {
            log.warn("!!! Error decrypt '{}'", encrypted);
            return null;
        }
    }

    public static boolean isRef(String value) {
        return !Strings.isNullOrEmpty(value) && value.charAt(0) == '$';
    }

    public static String markRef(String value) {
        return '$' + value;
    }
}
