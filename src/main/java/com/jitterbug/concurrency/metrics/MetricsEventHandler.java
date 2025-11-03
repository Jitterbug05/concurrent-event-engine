package com.jitterbug.concurrency.metrics;

import com.jitterbug.concurrency.event.Event;
import com.jitterbug.concurrency.processing.EventHandler;

public class MetricsEventHandler implements EventHandler {

    private final EventHandler delegate;
    private final MetricsCollector collector;

    public MetricsEventHandler(EventHandler delegate, MetricsCollector collector) {
        this.delegate = delegate;
        this.collector = collector;
    }

    @Override
    public void handle(Event event) {
        long start = System.nanoTime();
        delegate.handle(event);
        collector.recordEvent(event, start);
    }
}
