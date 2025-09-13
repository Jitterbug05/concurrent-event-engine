package com.jitterbug.concurrency.queue;

import com.jitterbug.concurrency.event.Event;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ManualBlockingQueue implements EventQueue {

    private final Event[] buffer;
    private int head = 0;
    private int tail = 0;
    private int size = 0;

    private final ReentrantLock putLock = new ReentrantLock();
    private final Condition notFull = putLock.newCondition();

    private final ReentrantLock takeLock = new ReentrantLock();
    private final Condition notEmpty = takeLock.newCondition();

    public ManualBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }

        this.buffer = new Event[capacity];
    }

    @Override
    public void publish(Event event) {
        int c = -1;

        putLock.lock();
        try {
            while (size == buffer.length) {
                try {
                    notFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            buffer[tail] = event;
            tail = (tail + 1) % buffer.length;
            c = ++size;

            if (c < buffer.length) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }

        if (c == 1) {
            signalNotEmpty();
        }
    }

    @Override
    public Event consume() throws InterruptedException {
        Event event;
        int c = -1;

        takeLock.lock();
        try {
            while (size == 0) {
                notEmpty.await();
            }

            event = buffer[head];
            head = (head + 1) % buffer.length;
            c = --size;

            if (c > 0) {
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }

        if (c == buffer.length - 1) {
            signalNotFull();
        }

        return event;
    }

    private void signalNotEmpty() {
        takeLock.lock();

        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    private void signalNotFull() {
        putLock.lock();

        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }
}
