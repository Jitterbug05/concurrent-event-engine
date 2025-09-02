package com.jitterbug.concurrency.event;

import java.util.concurrent.ThreadLocalRandom;

public enum Action {

    CLICK, VIEW, PURCHASE, LOGIN, LOGOUT;

    public static final int size = values().length;

    public static Action randomAction() {
        return Action.values()[ThreadLocalRandom.current().nextInt(size)];
    }
}
