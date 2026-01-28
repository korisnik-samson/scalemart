package com.samson.scalemart.streaming;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * IMPLEMENTATION: Change Data Capture (Chapter 11)
 * Simulates a "Log-Based Message Broker" (like Kafka).
 * Database writes are immutable events appended to this log.
 */
@Component
public class CDCLogPublisher {

    public record ChangeEvent(String table, String key, String oldValue, String newValue) {}

    // The "Log" - an append-only sequence of immutable records
    private final BlockingQueue<ChangeEvent> eventLog = new LinkedBlockingQueue<>();

    public void publishChange(String table, String key, String val) {
        ChangeEvent event = new ChangeEvent(table, key, null, val);
        eventLog.offer(event);

        notifyConsumers(event);
    }

    private void notifyConsumers(@NonNull ChangeEvent event) {
        // In a real system, this would push to a Kafka topic
        System.out.println("[CDC Stream] Event: Table=" + event.table() + " Key=" + event.key() + " -> " + event.newValue());

        // Simulating a derived data system (e.g., Search Index) updating itself
        updateSearchIndex(event);
    }

    private void updateSearchIndex(@NonNull ChangeEvent event) {
        System.out.println("[SearchIndex] Indexed document for key: " + event.key());
    }
}