package ru.javaops.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javaops.config.AppProperties;
import ru.javaops.model.User;
import ru.javaops.util.RefUtil;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class RefService {

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

    public String encrypt(String value) {
        return RefUtil.markRef(RefUtil.encrypt0(value, secretKey));
    }

    private String decrypt(String value) {
        return RefUtil.isRef(value) ? RefUtil.decrypt0(value.substring(1), secretKey) : null;
    }

    public User decryptUser(String value) {
        User user = null;
        String email = decrypt(value);
        if (email != null) {
            user = userService.findByEmail(email);
            if (user == null) {
                log.error("!!! Error user decrypted email '{}'", email);
            }
        }
        return user;
    }

    public User getRefUser(String channel) {
        if (RefUtil.isRef(channel)) {
            User user = userService.findByEmail(channel.substring(1));
            if (user == null) {
                log.error("!!! Error refUser channel '{}'", channel);
            }
            return user;
        }
        return null;
    }

    public String getRefUrl(String project, String email) {
        return String.format("http://javaops.ru/ref/%s/%s", project, encrypt(email));
    }
}