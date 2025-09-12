package com.jitterbug.concurrency.metrics;

import com.jitterbug.concurrency.event.Event;

public interface MetricsCollector {

    void recordEvent(Event event, long startTimeNanos);
    void report();
    void shutdown();
}
