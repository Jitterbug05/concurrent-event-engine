package com.jitterbug.concurrency.aggregation;

import com.jitterbug.concurrency.event.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockingAggregator implements EventAggregator {

    private final Map<Long, Long> userCounts = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void aggregate(Event event) {
        lock.writeLock().lock();

        try {
            userCounts.put(event.getUserId(),
                    userCounts.getOrDefault(event.getUserId(), 0L) + 1);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public long getCountForUser(long userId) {
        lock.readLock().lock();

        try {
            return userCounts.getOrDefault(userId, 0L);
        } finally {
            lock.readLock().unlock();
        }
    }
}
