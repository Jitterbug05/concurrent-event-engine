package com.jitterbug.concurrency.queue;

import com.jitterbug.concurrency.event.Event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueAdapter implements EventQueue {

    private final BlockingQueue<Event> queue;

    public BlockingQueueAdapter(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public void publish(Event event) {
        try {
            queue.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Event consume() throws InterruptedException {
        return queue.take();
    }
}
