package com.jitterbug.concurrency.metrics;

import com.jitterbug.concurrency.event.Event;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class CsvMetricsCollector implements MetricsCollector {

    private final AtomicLong eventCount = new AtomicLong();
    private final AtomicLong totalLatencyNanos = new AtomicLong();
    private final AtomicLong maxLatencyNanos = new AtomicLong();

    private final PrintWriter writer;
    private final long startTimeMillis;
    private volatile boolean running;

    public CsvMetricsCollector(String filePath) throws IOException {
        this.writer = new PrintWriter(new FileWriter(filePath));
        this.startTimeMillis = System.currentTimeMillis();
        this.running = true;

        writer.println("timestamp,eventsProcessed,avgLatencyMs,maxLatencyMs,usedMemoryMB");
    }

    @Override
    public void recordEvent(Event event, long startTimeMillis) {
        long latency = System.nanoTime() - startTimeMillis;
        eventCount.incrementAndGet();
        totalLatencyNanos.addAndGet(latency);
        maxLatencyNanos.accumulateAndGet(latency, Math::max);
    }

    @Override
    public void report() {
        long now = System.currentTimeMillis();
        long elapsedMillis = now - startTimeMillis;

        long events = eventCount.getAndSet(0);
        long totalLatency = totalLatencyNanos.getAndSet(0);
        long maxLatency = maxLatencyNanos.getAndSet(0);

        double avgLatencyMs = (events == 0) ? 0 : (totalLatency / 1_000_000.0) / events;
        double maxLatencyMs = maxLatency / 1_000_000.0;
        long usedMemMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

        writer.printf("%s,%d,%.3f,%.3f,%d%n",
                Instant.ofEpochMilli(now),
                events,
                avgLatencyMs,
                maxLatencyMs,
                usedMemMB);

        writer.flush();
    }

    @Override
    public void shutdown() {
        running = false;
        writer.close();
    }
}
