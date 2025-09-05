package com.jitterbug.concurrency.aggregation;

import com.jitterbug.concurrency.event.Event;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentAggregator implements EventAggregator {

    private final ConcurrentHashMap<Long, AtomicLong> userCounts = new ConcurrentHashMap<>();

    @Override
    public void aggregate(Event event) {
        userCounts.computeIfAbsent(event.getUserId(), id -> new AtomicLong())
                  .incrementAndGet();
    }

    @Override
    public long getCountForUser(long userId) {
        return userCounts.getOrDefault(userId, new AtomicLong(0)).get();
    }
}
