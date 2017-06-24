package ru.javaops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * GKislin
 * 20.08.2015.
 */
@ConfigurationProperties("app.integration")
@Validated
public class IntegrationProperties {

    @NotNull
    private String vkToken;

    @NotNull
    private Map<String, String> slackTokens;

    public String getVkToken() {
        return vkToken;
    }

    public void setVkToken(String vkToken) {
        this.vkToken = vkToken;
    }

    public Map<String, String> getSlackTokens() {
        return slackTokens;
    }

    public void setSlackTokens(Map<String, String> slackTokens) {
        this.slackTokens = slackTokens;
    }

    public String getSlackToken(String teamName) {
        return slackTokens.get(teamName);
    }
}