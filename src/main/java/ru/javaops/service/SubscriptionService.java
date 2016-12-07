package ru.javaops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javaops.config.AppProperties;
import ru.javaops.util.PasswordUtil;

@Service
public class SubscriptionService {

    @Autowired
    private AppProperties appProperties;

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

    private String getSalted(String email) {
        return email + appProperties.getActivationSecretSalt();
    }
}
