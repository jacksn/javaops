package ru.javaops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

/**
 * GKislin
 * 20.08.2015.
 */
@ConfigurationProperties("app.integration")
public class IntegrationProperties {

    /**
     * VK token
     */
    @NotNull
    private String vkToken;

    @NotNull
    private String slackToken;

    public String getVkToken() {
        return vkToken;
    }

    public void setVkToken(String vkToken) {
        this.vkToken = vkToken;
    }

    public String getSlackToken() {
        return slackToken;
    }

    public void setSlackToken(String slackToken) {
        this.slackToken = slackToken;
    }
}
