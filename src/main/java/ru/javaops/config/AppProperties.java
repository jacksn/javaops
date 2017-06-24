package ru.javaops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * GKislin
 * 20.08.2015.
 */
@ConfigurationProperties("app")
@Validated
public class AppProperties {

    /**
     * Test email
     */
    @NotNull
    private String email;

    /**
     * Interval for update templates
     */
    @NotNull
    private int cacheSeconds;

    /**
     * Secret for generate activation key
     */
    @NotNull
    private String activationSecretSalt;

    /**
     * Secret key
     */
    @NotNull
    private String secretKey;

    /**
     * Host url
     */
    @NotNull
    private String hostUrl;

    public String getActivationSecretSalt() {
        return activationSecretSalt;
    }

    public void setActivationSecretSalt(String activationSecretSalt) {
        this.activationSecretSalt = activationSecretSalt;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setCacheSeconds(int cacheSeconds) {
        this.cacheSeconds = cacheSeconds;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCacheSeconds() {
        return cacheSeconds;
    }

    public String getHostUrl() {
        return hostUrl;
    }
}
