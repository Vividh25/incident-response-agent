package com.vividh.incident.agent.kafka;

import com.vividh.incident.agent.event.AlertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AlertListener {

    private static final Logger log = LoggerFactory.getLogger(AlertListener.class);

    @KafkaListener(topics = "${incident.alerts-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void onAlert(AlertEvent event) {
        log.info("Received alert: source={} metric={} value={} threshold={}",
                event.source(), event.metric(), event.value(), event.threshold());

        // Next up: hand this off to the diagnostic agent, which starts the
        // ReAct loop - reason about the alert, call tools to gather more
        // context (logs, metrics), and propose a remediation plan.
    }
}
