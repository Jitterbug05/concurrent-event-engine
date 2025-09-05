package com.jitterbug.concurrency.aggregation;

import com.jitterbug.concurrency.event.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class BatchingAggregator implements EventAggregator {

    private final ConcurrentHashMap<Long, AtomicLong> globalCounts = new ConcurrentHashMap<>();
    private final ThreadLocal<Map<Long, Long>> localBuffer = ThreadLocal.withInitial(HashMap::new);
    private final int batchSize;

    public BatchingAggregator(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public void aggregate(Event event) {
        Map<Long, Long> buffer = localBuffer.get();
        buffer.merge(event.getUserId(), 1L, Long::sum);

        if (buffer.size() >= batchSize) {
            flush(buffer);
        }
    }

    private void flush(Map<Long, Long> buffer) {
        buffer.forEach((userId, count) ->
                globalCounts.computeIfAbsent(userId, id -> new AtomicLong())
                            .addAndGet(count)
        );

        buffer.clear();
    }

    @Override
    public long getCountForUser(long userId) {
        return globalCounts.getOrDefault(userId, new AtomicLong(0L)).get();
    }
}
