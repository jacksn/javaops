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
    private StamplayProps stamplay;

    public String getVkToken() {
        return vkToken;
    }

    public void setVkToken(String vkToken) {
        this.vkToken = vkToken;
    }

    public StamplayProps getStamplay() {
        return stamplay;
    }

    public void setStamplay(StamplayProps stamplay) {
        this.stamplay = stamplay;
    }

    public static class StamplayProps {
        private String appId;
        private String apiKey;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }
}
