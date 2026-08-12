package com.vividh.incident.agent.diagnosis;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Slf4j
@Service
public class RemediationExecutor {

    private final RestClient appRestClient;

    public RemediationExecutor(@Qualifier("appRestClient") RestClient appRestClient) {
        this.appRestClient = appRestClient;
    }

    public void execute(ApprovalStore.Approval approval) {
        switch (approval.action()) {
            case RESTART_SERVICE -> {
                try {
                    appRestClient.post()
                            .uri("/simulate/reset")
                            .retrieve()
                            .toBodilessEntity();
                    log.info("Restart initiated for service {}", approval.serviceName());
                } catch(RestClientException e) {
                    log.error("Request failed with: {} for service: {}", e.getLocalizedMessage(), approval.serviceName());
                }
            }
            case SCALE_UP, ROLL_BACK -> log.info("Not yet implemented");
        }
    }
}
