package com.vividh.incident.toyapp.kafka;

import com.vividh.incident.toyapp.event.AlertEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AlertPublisher {

    private final KafkaTemplate<String, AlertEvent> kafkaTemplate;
    private final String topic;

    public AlertPublisher(KafkaTemplate<String, AlertEvent> kafkaTemplate,
                           @Value("${incident.alerts-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(AlertEvent event) {
        // Keying by source means all alerts for the same producing service
        // land on the same partition, so the agent-service can process a
        // given source's alerts in order if it ever needs to.
        kafkaTemplate.send(topic, event.source(), event);
    }
}
