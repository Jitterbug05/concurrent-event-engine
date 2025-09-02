package com.jitterbug.concurrency.processing;

import com.jitterbug.concurrency.event.Event;
import com.jitterbug.concurrency.queue.EventQueue;

import java.util.concurrent.ForkJoinPool;

public class ForkJoinEventProcessor implements EventProcessor {

    private final ForkJoinPool pool;
    private volatile boolean running;

    public ForkJoinEventProcessor(int parralelism) {
        this.pool = new ForkJoinPool(parralelism);
        this.running = true;
    }

    @Override
    public void start(EventQueue queue, EventHandler handler) {
        pool.submit(() -> {
            while (running) {
                try {
                    Event event = queue.consume();
                    pool.submit(() -> handler.handle(event));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    @Override
    public void stop() {
        running = false;
        pool.shutdownNow();
    }
}
