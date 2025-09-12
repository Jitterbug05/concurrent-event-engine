package com.jitterbug.concurrency.metrics;

import java.util.Timer;
import java.util.TimerTask;

public class MetricsScheduler {

    private final Timer timer = new Timer(true);

    public void schedule(MetricsCollector collector, long intervalMillis) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                collector.report();
            }
        }, intervalMillis, intervalMillis);
    }

    public void shutdown() {
        timer.cancel();
    }
}
