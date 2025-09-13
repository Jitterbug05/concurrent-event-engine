package com.jitterbug.concurrency.queue;

import com.jitterbug.concurrency.event.Action;
import com.jitterbug.concurrency.event.Event;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

public class ManualBlockingQueueTest {

    @Test
    void testPublishAndConsume() throws InterruptedException {
        ManualBlockingQueue queue = new ManualBlockingQueue(2);
        Event event = new Event(1, 42, Instant.now(), Action.LOGIN);

        queue.publish(event);

        assertEquals(event.getUserId(), queue.consume().getUserId());
    }

    @Test
    void testBlocksWhenFull() throws InterruptedException {
        ManualBlockingQueue queue = new ManualBlockingQueue(1);
        queue.publish(new Event(1, 42, Instant.now(), Action.LOGIN));

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> blockedPut = executorService.submit(() -> queue.publish(new Event(2, 59, Instant.now(), Action.LOGIN)));

        Thread.sleep(200);
        assertFalse(blockedPut.isDone());

        queue.consume();

        Thread.sleep(200);
        assertTrue(blockedPut.isDone());
    }

    @Test
    void testBlocksWhenEmpty() throws InterruptedException {
        ManualBlockingQueue queue = new ManualBlockingQueue(1);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> blockedConsume = executorService.submit(queue::consume);

        Thread.sleep(200);
        assertFalse(blockedConsume.isDone());

        queue.publish(new Event(1, 42, Instant.now(), Action.LOGIN));

        Thread.sleep(200);
        assertTrue(blockedConsume.isDone());
    }
}
