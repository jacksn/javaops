package ru.javaops.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javaops.config.AppProperties;
import ru.javaops.config.exception.NoPartnerException;
import ru.javaops.model.User;
import ru.javaops.util.PasswordUtil;

@Service
@Slf4j
public class SubscriptionService {
    private static final String PARTNER_GROUP_NAME = "partner";

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private UserService userService;

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
}
