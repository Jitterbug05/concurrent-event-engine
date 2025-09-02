package com.jitterbug.concurrency.processing;

import com.jitterbug.concurrency.event.Event;
import com.jitterbug.concurrency.queue.EventQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorEventProcessor implements EventProcessor {

    private final int numWorkers;
    private ExecutorService executorService;
    private final List<Runnable> workers;

    public ExecutorEventProcessor(int numWorkers) {
        this.numWorkers = numWorkers;
        this.workers = new ArrayList<>();
    }

    @Override
    public void start(EventQueue queue, EventHandler handler) {
        executorService = Executors.newFixedThreadPool(numWorkers);

        for (int i = 0; i < numWorkers; i++) {
            Runnable worker = () -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Event event = queue.consume();
                        handler.handle(event);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };

            workers.add(worker);
            executorService.submit(worker);
        }
    }

    @Override
    public void stop() {
        executorService.shutdownNow();
    }
}
