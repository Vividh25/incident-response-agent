package com.vividh.incident.toyapp.controller;

import com.vividh.incident.toyapp.service.RequestOutcomeTracker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class FailureController {

    private final RequestOutcomeTracker tracker;
    private final AtomicBoolean errorSpikeEnabled = new AtomicBoolean(false);
    private final AtomicBoolean latencySpikeEnabled = new AtomicBoolean(false);
    private final Random random = new Random();

    public FailureController(RequestOutcomeTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Simulates normal application traffic. Call this repeatedly (a shell
     * loop, or any load tool) to generate the request stream HealthMonitor
     * watches. When errorSpikeEnabled is on, ~60% of calls fail.
     */
    @GetMapping("/work")
    public Map<String, Object> work() {
        if (latencySpikeEnabled.get()) {
            sleepQuietly(800);
        }
        if (errorSpikeEnabled.get() && random.nextDouble() < 0.6) {
            tracker.recordFailure();
            throw new RuntimeException("Simulated downstream failure");
        }
        tracker.recordSuccess();
        return Map.of("status", "ok");
    }

    @PostMapping("/simulate/error-spike")
    public Map<String, Object> toggleErrorSpike(@RequestParam boolean enabled) {
        errorSpikeEnabled.set(enabled);
        return Map.of("errorSpikeEnabled", enabled);
    }

    @PostMapping("/simulate/latency-spike")
    public Map<String, Object> toggleLatencySpike(@RequestParam boolean enabled) {
        latencySpikeEnabled.set(enabled);
        return Map.of("latencySpikeEnabled", enabled);
    }

    @PostMapping("/simulate/reset")
    public Map<String, Object> reset() {
        errorSpikeEnabled.set(false);
        latencySpikeEnabled.set(false);
        tracker.reset();
        return Map.of("reset", true);
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
