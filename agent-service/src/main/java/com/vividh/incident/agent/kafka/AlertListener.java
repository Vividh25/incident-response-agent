package com.vividh.incident.agent.kafka;

import com.vividh.incident.agent.diagnosis.ApprovalStore;
import com.vividh.incident.agent.event.AlertEvent;
import com.vividh.incident.agent.diagnosis.DiagnosisResult;
import com.vividh.incident.agent.diagnosis.DiagnosticAgent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class AlertListener {

    private static final Logger log = LoggerFactory.getLogger(AlertListener.class);
    private final DiagnosticAgent diagnosticAgent;
    private final ApprovalStore approvalStore;
    private final ConcurrentHashMap<String, Instant> alertMap = new ConcurrentHashMap<>();
    private static final Duration COOLDOWN = Duration.ofMinutes(2);

    @KafkaListener(topics = "${incident.alerts-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void onAlert(AlertEvent event) {

        AtomicBoolean isDuplicate = new AtomicBoolean(false);
        alertMap.compute(event.source(), (source, lastDiagnosedAt) -> {
            Instant now = Instant.now();
            if (lastDiagnosedAt != null && Duration.between(lastDiagnosedAt, now).compareTo(COOLDOWN) < 0) {
                isDuplicate.set(true);
                return lastDiagnosedAt; // keep the original timestamp - don't reset the cooldown window
            }
            return now;
        });

        if (isDuplicate.get()) {
            log.info("Duplicate event detected for {}", event.source());
            return;
        }

        DiagnosisResult result = diagnosticAgent.diagnose(event);

        if (result.getStatus() == DiagnosisResult.Status.SUCCESS) {
            log.info("SUCCESS");
            log.info(result.getDiagnosis());
        }
        else if (result.getStatus() == DiagnosisResult.Status.API_ERROR) {
            log.error("API ERROR");
            log.error("Failed to get diagnosis for {}", event.source());
        }
        else if (result.getStatus() == DiagnosisResult.Status.REMEDIATION_PROPOSAL){
            UUID id = approvalStore.addApproval(result.getProposal());
            log.info("Approval added to store. ID: {}", id);
        }

        else if (result.getStatus() == DiagnosisResult.Status.MAX_ITERATIONS_EXCEEDED){
            log.error("Maximum iterations exceeded for {}", event.source());
        }
    }
}
