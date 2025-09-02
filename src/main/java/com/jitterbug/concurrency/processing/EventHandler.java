package com.jitterbug.concurrency.processing;

import com.jitterbug.concurrency.event.Event;

public interface EventHandler {

    void handle(Event event);
}
