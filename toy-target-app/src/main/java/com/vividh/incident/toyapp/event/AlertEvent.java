package com.vividh.incident.toyapp.event;

import java.time.Instant;
import java.util.Map;

/**
 * The generic event contract every producing service publishes to the
 * "incident-alerts" Kafka topic. The agent-service will only ever depend on
 * this shape - never on any specific producer's internals. That's the whole
 * trick that makes the agent reusable across applications instead of tied
 * to one company's monitoring stack.
 */
public record AlertEvent(
        String source,
        String severity,
        String metric,
        double value,
        double threshold,
        Instant timestamp,
        Map<String, String> context
) {
}
