package com.jitterbug.concurrency.event;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class EventGenerator {

    private final AtomicLong idCounter = new AtomicLong();

    private Event nextEvent() {
        long id = idCounter.incrementAndGet();

        return Event.randomEvent(id);
    }

    public void eventStream(int eventsPerSecond, Consumer<Event> consumer) {
        Thread generatorThread = new Thread(() -> {
            long intervalMillis = 1000L / eventsPerSecond;
            while (!Thread.currentThread().isInterrupted()) {
                Event event = nextEvent();
                consumer.accept(event);

                try {
                    Thread.sleep(intervalMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        generatorThread.setDaemon(true);
        generatorThread.start();
    }
}
