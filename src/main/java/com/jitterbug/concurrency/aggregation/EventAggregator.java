package com.jitterbug.concurrency.aggregation;

import com.jitterbug.concurrency.event.Event;

public interface EventAggregator {

    void aggregate(Event event);
    long getCountForUser(long userId);
}
