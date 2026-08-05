package com.vividh.incident.toyapp.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Deliberately simple: just counts successes/failures since the last time
 * HealthMonitor read and reset them. A real monitoring pipeline would use a
 * proper sliding time window (or push raw metrics to something like
 * Prometheus and query it), but plain counters keep this project focused on
 * the agent, not on metrics-pipeline plumbing.
 */
@Component
public class RequestOutcomeTracker {

    private final AtomicInteger successes = new AtomicInteger(0);
    private final AtomicInteger failures = new AtomicInteger(0);

    public void recordSuccess() {
        successes.incrementAndGet();
    }

    public void recordFailure() {
        failures.incrementAndGet();
    }

    /** Reads the current counts and resets them to zero atomically-enough for our purposes. */
    public Snapshot snapshotAndReset() {
        int s = successes.getAndSet(0);
        int f = failures.getAndSet(0);
        return new Snapshot(s, f);
    }

    public void reset() {
        successes.set(0);
        failures.set(0);
    }

    public record Snapshot(int successes, int failures) {
        public int total() {
            return successes + failures;
        }

        public double errorRate() {
            return total() == 0 ? 0.0 : (double) failures / total();
        }
    }
}
