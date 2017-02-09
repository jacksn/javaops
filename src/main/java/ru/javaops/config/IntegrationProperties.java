package ru.javaops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * GKislin
 * 20.08.2015.
 */
@ConfigurationProperties("app.integration")
public class IntegrationProperties {

    @NotNull
    private String vkToken;

    @NotNull
    private Map<String, String> slackTokens;

    @NotNull
    private Map<String, String> googleGroups;

    public String getVkToken() {
        return vkToken;
    }

    public void setVkToken(String vkToken) {
        this.vkToken = vkToken;
    }

    public void setSlackTokens(Map<String, String> slackTokens) {
        this.slackTokens = slackTokens;
    }

    public String getSlackToken(String teamName) {
        return slackTokens.get(teamName);
    }

    public void setGoogleGroups(Map<String, String> googleGroups) {
        this.googleGroups = googleGroups;
    }

    public String getGoogleGroup(String groupName) {
        return slackTokens.get(groupName);
    }
}