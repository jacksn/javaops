package ru.javaops.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import ru.javaops.config.IntegrationProperties;

@Service
public class IntegrationService {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationService.class);

    @Autowired
    private JsonService jsonService;

    @Autowired
    private IntegrationProperties integrationProperties;

    public void asyncSendSlackInvitation(String email, String project) {
        new AsyncRestTemplate().exchange(
                "https://slack.com/api/users.admin.invite?token={token}&email={email}",
                HttpMethod.GET, null, String.class, integrationProperties.getSlackToken(project), email).addCallback(
                res -> LOG.info("Slack invitation result: " + res.getStatusCode() + ": " + res.getBody()),
                ex -> LOG.error("Slack invitation result", ex)
        );
    }

    public SlackResponse sendSlackInvitation(String email, String project) {
        ResponseEntity<String> response = new RestTemplate().exchange(
                "https://slack.com/api/users.admin.invite?token={token}&email={email}",
                HttpMethod.GET, null, String.class, integrationProperties.getSlackToken(project), email);

        return response.getStatusCode().is2xxSuccessful() ?
                jsonService.readValue(response.getBody(), SlackResponse.class) :
                new SlackResponse(false, response.getBody());
    }

    /**
     * gkislin
     * 03.06.2016
     */
    public static class SlackResponse {
        final boolean ok;
        final String error;

        public SlackResponse(@JsonProperty("ok") boolean ok, @JsonProperty(value = "error") String error) {
            this.ok = ok;
            this.error = error;
        }

        public boolean isOk() {
            return ok;
        }

        public String getError() {
            return error;
        }
    }
}
