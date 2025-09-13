package com.jitterbug.concurrency;

import com.jitterbug.concurrency.aggregation.ConcurrentAggregator;
import com.jitterbug.concurrency.aggregation.EventAggregator;
import com.jitterbug.concurrency.event.Action;
import com.jitterbug.concurrency.event.Event;
import com.jitterbug.concurrency.metrics.CsvMetricsCollector;
import com.jitterbug.concurrency.metrics.MetricsEventHandler;
import com.jitterbug.concurrency.processing.AggregatingEventHandler;
import com.jitterbug.concurrency.processing.ExecutorEventProcessor;
import com.jitterbug.concurrency.queue.BlockingQueueAdapter;
import com.jitterbug.concurrency.queue.EventQueue;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTest {

    @Test
    void testFullPipelineEventProcessing() throws Exception {
        EventQueue queue = new BlockingQueueAdapter(100);

        EventAggregator aggregator = new ConcurrentAggregator();
        AggregatingEventHandler handler = new AggregatingEventHandler(aggregator);

        CsvMetricsCollector metricsCollector = new CsvMetricsCollector("test-metrics.csv");
        MetricsEventHandler metricsEventHandler = new MetricsEventHandler(handler, metricsCollector);

        ExecutorEventProcessor processor = new ExecutorEventProcessor(2);
        processor.start(queue, metricsEventHandler);

        for (int i=0; i<10; i++) {
            queue.publish(new Event(i, 42, Instant.now(), Action.CLICK));
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {

        } finally {
            processor.stop();
            metricsCollector.shutdown();
        }

        assertTrue(aggregator.getCountForUser(42) >= 10);

        File f = new File("test-metrics.csv");
        assertTrue(f.exists());
        assertTrue(Files.readString(f.toPath()).contains("eventsProcessed"));

        f.delete();
    }
}
