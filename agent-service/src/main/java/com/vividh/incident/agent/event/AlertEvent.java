package com.vividh.incident.agent.event;

import java.time.Instant;
import java.util.Map;

/**
 * Mirrors toy-target-app's AlertEvent exactly - field names and types must
 * match, since this is what Kafka's JsonDeserializer will map incoming JSON
 * onto. Duplicated for now rather than shared: with only two services it's
 * not worth the ceremony of a shared library module yet. Worth extracting
 * into a common module once a third producer/consumer shows up, or once the
 * contract needs to change and you don't want to update it in two places.
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
