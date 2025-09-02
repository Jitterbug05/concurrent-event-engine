package com.jitterbug.concurrency.event;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class Event {

    private final long id;
    private final long userId;
    private final Instant timestamp;
    private final Action actionType;

    public Event(long id, long userId, Instant timestamp, Action actionType) {
        this.id = id;
        this.userId = userId;
        this.timestamp = timestamp;
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                ", actionType=" + actionType +
                "}";
    }

    public static Event randomEvent(long id) {
        long userId = ThreadLocalRandom.current().nextLong(1, 1000);
        Action action = Action.randomAction();

        return new Event(id, userId, Instant.now(), action);
    }
}
