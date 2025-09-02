package com.jitterbug.concurrency.processing;

import com.jitterbug.concurrency.queue.EventQueue;

public interface EventProcessor {

    void start(EventQueue queue, EventHandler handler);
    void stop();
}
