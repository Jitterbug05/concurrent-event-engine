package com.jitterbug.concurrency.metrics;

import com.jitterbug.concurrency.event.Event;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class CsvMetricsCollector implements MetricsCollector {
    // periodic counters
    private final AtomicLong eventCount = new AtomicLong();
    private final AtomicLong totalLatencyNanos = new AtomicLong();
    private final AtomicLong maxLatencyNanos = new AtomicLong();

    // cumulative totals for final summary
    private final AtomicLong totalEventsAllTime = new AtomicLong();
    private final AtomicLong totalLatencyAllTime = new AtomicLong();
    private final AtomicLong globalMaxLatency = new AtomicLong();

    private final PrintWriter writer;
    private final long startTimeMillis;
    private volatile boolean running;

    public CsvMetricsCollector(String filePath) throws IOException {
        this.writer = new PrintWriter(new FileWriter(filePath));
        this.startTimeMillis = System.currentTimeMillis();
        this.running = true;

        writer.println("timestamp,eventsProcessed,avgLatencyMicros,maxLatencyMicros,usedMemoryMB");
    }

    @Override
    public void recordEvent(Event event, long startTimeNanos) {
        long latency = System.nanoTime() - startTimeNanos;

        eventCount.incrementAndGet();
        totalLatencyNanos.addAndGet(latency);
        maxLatencyNanos.accumulateAndGet(latency, Math::max);

        // accumulate for final summary
        totalEventsAllTime.incrementAndGet();
        totalLatencyAllTime.addAndGet(latency);
        globalMaxLatency.accumulateAndGet(latency, Math::max);
    }

    @Override
    public void report() {
        long now = System.currentTimeMillis();
        long elapsedMillis = now - startTimeMillis;

        long events = eventCount.getAndSet(0);
        long totalLatency = totalLatencyNanos.getAndSet(0);
        long maxLatency = maxLatencyNanos.getAndSet(0);

        double avgLatencyMicros = (events == 0) ? 0 : (totalLatency / 1000.0) / events;
        double maxLatencyMicros = maxLatency / 1000.0;
        long usedMemMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

        writer.printf("%s,%d,%.3f,%.3f,%d%n",
                Instant.ofEpochMilli(now),
                events,
                avgLatencyMicros,
                maxLatencyMicros,
                usedMemMB);
        writer.flush();
    }

    @Override
    public void shutdown() {
        running = false;
        writer.close();
    }

    public void printFinalSummary() {
        long endTimeMillis = System.currentTimeMillis();
        double runSeconds = (endTimeMillis - startTimeMillis) / 1000.0;

        long totalEvents = totalEventsAllTime.get();
        double avgLatencyMicros =
                (totalEvents == 0) ? 0 : (totalLatencyAllTime.get() / 1000.0) / totalEvents;
        double maxLatencyMicros = globalMaxLatency.get() / 1000.0;
        double throughput = (totalEvents / runSeconds);

        long usedMemMB =
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

        System.out.println("\n========== FINAL METRICS SUMMARY ==========");
        System.out.printf("Runtime: %.2f seconds%n", runSeconds);
        System.out.printf("Total events: %,d%n", totalEvents);
        System.out.printf("Throughput: %.2f events/sec%n", throughput);
        System.out.printf("Average latency: %.3f µs%n", avgLatencyMicros);
        System.out.printf("Maximum latency: %.3f µs%n", maxLatencyMicros);
        System.out.printf("Used memory (approx): %d MB%n", usedMemMB);
        System.out.println("===========================================");
    }
}
