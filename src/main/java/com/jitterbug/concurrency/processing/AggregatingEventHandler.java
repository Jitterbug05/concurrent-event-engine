package com.jitterbug.concurrency.processing;

import com.jitterbug.concurrency.aggregation.EventAggregator;
import com.jitterbug.concurrency.event.Event;

public class AggregatingEventHandler implements EventHandler {

    private final EventAggregator aggregator;

    public AggregatingEventHandler(EventAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void handle(Event event) {
        aggregator.aggregate(event);
    }
}
