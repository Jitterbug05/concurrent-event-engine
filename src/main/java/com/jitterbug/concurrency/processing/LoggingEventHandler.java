package com.jitterbug.concurrency.processing;

import com.jitterbug.concurrency.event.Event;

public class LoggingEventHandler implements EventHandler {

    @Override
    public void handle(Event event) {
        System.out.println("Processed: " + event);
    }
}
