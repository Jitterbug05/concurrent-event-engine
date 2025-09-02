package com.jitterbug.concurrency.queue;

import com.jitterbug.concurrency.event.Event;

public interface EventQueue {

    void publish (Event event);
    Event consume() throws InterruptedException;
}
