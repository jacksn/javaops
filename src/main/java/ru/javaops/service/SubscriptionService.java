package ru.javaops.service;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javaops.config.AppProperties;
import ru.javaops.config.exception.NoPartnerException;
import ru.javaops.model.User;
import ru.javaops.util.PasswordUtil;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class SubscriptionService {
    private static final String PARTNER_GROUP_NAME = "partner";

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private UserService userService;

    private SecretKey secretKey;

    @PostConstruct
    private void postConstruct() throws NoSuchAlgorithmException {
//        http://stackoverflow.com/questions/10303767/encrypt-and-decrypt-in-java
//        http://sakthipriyan.com/2015/07/21/encryption-and-decrption-in-java.html

        this.secretKey = new SecretKeySpec(appProperties.getSecretKey().getBytes(), "AES");
    }

    public String getSubscriptionUrl(String email, String activationKey, boolean active) {
        return appProperties.getHostUrl() + "/activate?email=" + email + "&key=" + activationKey + "&activate=" + active;
    }

    public String generateActivationKey(String email) {
        return PasswordUtil.getPasswordEncoder().encode(getSalted(email));
    }

    public void checkActivationKey(String value, String key) {
        if (!PasswordUtil.isMatch(getSalted(value), key)) {
            throw new IllegalArgumentException("Неверный ключ активации");
        }
    }

    private String getSalted(String value) {
        return value + appProperties.getActivationSecretSalt();
    }

    public User checkPartner(String partnerKey) {
        User partner = userService.findByEmailAndGroupName(partnerKey.toLowerCase(), PARTNER_GROUP_NAME);
        if (partner == null) {
            throw new NoPartnerException(partnerKey);
        }
        return partner;
    }

    public void checkAdminKey(String adminKey) {
        if (!userService.findExistedByEmail(adminKey).isAdmin()) {
            throw new IllegalArgumentException("Неверный ключ");
        }
    }

    public String encrypt(String value) {
        return "-" + encrypt0(value, secretKey);
    }

    public String decrypt(String value) {
        return !Strings.isNullOrEmpty(value) && value.charAt(0) == '-' ?
                decrypt0(value.substring(1), secretKey) : null;
    }

    static String encrypt0(String value, SecretKey secretKey) {
        try {
            byte[] bytes = value.getBytes();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedByte = cipher.doFinal(bytes);
            return Base64.getUrlEncoder().encodeToString(encryptedByte);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static String decrypt0(String encrypted, SecretKey secretKey) {
        try {
            byte[] encryptedByte = Base64.getUrlDecoder().decode(encrypted);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(encryptedByte);
            return new String(bytes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
