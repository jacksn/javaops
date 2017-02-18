package ru.javaops.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import ru.javaops.config.IntegrationProperties;

@Service
@Slf4j
public class IntegrationService {

    @Autowired
    private JsonService jsonService;

    @Autowired
    private IntegrationProperties integrationProperties;

    public void asyncSendSlackInvitation(String email, String project) {
        new AsyncRestTemplate().exchange(
                "https://slack.com/api/users.admin.invite?token={token}&email={email}",
                HttpMethod.GET, null, String.class, integrationProperties.getSlackToken(project), email).addCallback(
                res -> log.info("Slack invitation result: " + res.getStatusCode() + ": " + res.getBody()),
                ex -> log.error("Slack invitation result", ex)
        );
    }

    public SlackResponse sendSlackInvitation(String email, String project) {
        log.info("++++ Send SlackInvitation to {} ({})", email, project);
        ResponseEntity<String> response = new RestTemplate().exchange(
                "https://slack.com/api/users.admin.invite?token={token}&email={email}",
                HttpMethod.GET, null, String.class, integrationProperties.getSlackToken(project), email);

        SlackResponse slackResponse = response.getStatusCode().is2xxSuccessful() ?
                jsonService.readValue(response.getBody(), SlackResponse.class) :
                new SlackResponse(false, response.getBody());
        log.info(slackResponse.toString());
        return slackResponse;
    }

    /**
     * gkislin
     * 03.06.2016
     */
    @Value
    public static class SlackResponse {
        final boolean ok;
        final String error;

        public SlackResponse(@JsonProperty("ok") boolean ok, @JsonProperty(value = "error") String error) {
            this.ok = ok;
            this.error = error;
        }
    }
}
