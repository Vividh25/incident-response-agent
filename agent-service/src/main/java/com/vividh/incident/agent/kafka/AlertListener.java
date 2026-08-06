package com.vividh.incident.agent.kafka;

import com.vividh.incident.agent.event.AlertEvent;
import com.vividh.incident.agent.llm.DiagnosisResult;
import com.vividh.incident.agent.llm.DiagnosticAgent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertListener {

    private static final Logger log = LoggerFactory.getLogger(AlertListener.class);
    private final DiagnosticAgent diagnosticAgent;

    @KafkaListener(topics = "${incident.alerts-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void onAlert(AlertEvent event) {
        DiagnosisResult result = diagnosticAgent.diagnose(event);
        if (result.getStatus() == DiagnosisResult.Status.SUCCESS) {
            log.info("SUCCESS");
            log.info(result.getDiagnosis());
        }
        else if (result.getStatus() == DiagnosisResult.Status.API_ERROR) {
            log.error("API ERROR");
            log.error("Failed to get diagnosis for {}", event.source());
        }
        else {
            log.info("Maximum iterations exceeded for {}", event.source());
        }
    }
}
