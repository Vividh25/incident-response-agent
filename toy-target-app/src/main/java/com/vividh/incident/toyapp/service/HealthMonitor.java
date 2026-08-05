package com.vividh.incident.toyapp.service;

import com.vividh.incident.toyapp.event.AlertEvent;
import com.vividh.incident.toyapp.kafka.AlertPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * Polls RequestOutcomeTracker on a fixed interval and, if the error rate for
 * that window crosses the configured threshold, publishes an AlertEvent.
 * This is the piece the diagnostic agent will eventually be reacting to -
 * it plays the same role your real HealthMonitor / drift-detection code
 * would play in the actual Model Monitoring service.
 */
@Component
public class HealthMonitor {

    private final RequestOutcomeTracker tracker;
    private final AlertPublisher alertPublisher;
    private final double errorRateThreshold;
    private final String sourceName;

    public HealthMonitor(RequestOutcomeTracker tracker,
                          AlertPublisher alertPublisher,
                          @Value("${incident.error-rate-threshold}") double errorRateThreshold,
                          @Value("${spring.application.name}") String sourceName) {
        this.tracker = tracker;
        this.alertPublisher = alertPublisher;
        this.errorRateThreshold = errorRateThreshold;
        this.sourceName = sourceName;
    }

    @Scheduled(fixedRate = 10_000)
    public void checkHealth() {
        RequestOutcomeTracker.Snapshot snapshot = tracker.snapshotAndReset();
        if (snapshot.total() == 0) {
            return; // nothing happened this window, nothing to evaluate
        }

        double errorRate = snapshot.errorRate();
        if (errorRate >= errorRateThreshold) {
            alertPublisher.publish(new AlertEvent(
                    sourceName,
                    "critical",
                    "error_rate",
                    errorRate,
                    errorRateThreshold,
                    Instant.now(),
                    Map.of(
                            "window_requests", String.valueOf(snapshot.total()),
                            "window_failures", String.valueOf(snapshot.failures())
                    )
            ));
        }
    }
}
