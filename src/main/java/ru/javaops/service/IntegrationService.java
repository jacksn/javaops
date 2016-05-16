package ru.javaops.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;
import ru.javaops.config.IntegrationProperties;

@Service
public class IntegrationService {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationService.class);

    @Autowired
    private IntegrationProperties integrationProperties;

    public void asyncSendSlackInvitation(String email) {
        new AsyncRestTemplate().exchange(
                "https://slack.com/api/users.admin.invite?token={token}&email={email}&channels=#entrance_lesson,#git,#idea,#interview_job",
                HttpMethod.GET, null, String.class, integrationProperties.getSlackToken(), email).addCallback(
                res -> LOG.info("Slack invitation result: " + res.getStatusCode() + ": " + res.getBody()),
                ex -> LOG.error("Slack invitation result", ex)
        );
    }
}
