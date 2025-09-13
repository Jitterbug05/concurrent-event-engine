package com.jitterbug.concurrency.aggregation;

import com.jitterbug.concurrency.event.Action;
import com.jitterbug.concurrency.event.Event;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AggregatorTests {

    @Test
    void testSingleThreadAggregation() {
        ConcurrentAggregator aggregator = new ConcurrentAggregator();
        aggregator.aggregate(new Event(1, 42, Instant.now(), Action.LOGIN));
        aggregator.aggregate(new Event(2, 42, Instant.now(), Action.CLICK));

        assertEquals(2, aggregator.getCountForUser(42));
    }

    @Test
    void testMultipleUsers() {
        ConcurrentAggregator aggregator = new ConcurrentAggregator();
        aggregator.aggregate(new Event(1, 42, Instant.now(), Action.LOGIN));
        aggregator.aggregate(new Event(2, 42, Instant.now(), Action.CLICK));
        aggregator.aggregate(new Event(3, 59, Instant.now(), Action.LOGIN));

        assertEquals(2, aggregator.getCountForUser(42));
        assertEquals(1, aggregator.getCountForUser(59));
    }
}
