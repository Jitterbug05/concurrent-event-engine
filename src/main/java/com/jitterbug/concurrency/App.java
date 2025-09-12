package com.jitterbug.concurrency;

import com.jitterbug.concurrency.aggregation.ConcurrentAggregator;
import com.jitterbug.concurrency.aggregation.EventAggregator;
import com.jitterbug.concurrency.event.EventGenerator;
import com.jitterbug.concurrency.metrics.CsvMetricsCollector;
import com.jitterbug.concurrency.metrics.MetricsEventHandler;
import com.jitterbug.concurrency.metrics.MetricsScheduler;
import com.jitterbug.concurrency.processing.AggregatingEventHandler;
import com.jitterbug.concurrency.processing.EventHandler;
import com.jitterbug.concurrency.processing.ExecutorEventProcessor;
import com.jitterbug.concurrency.processing.LoggingEventHandler;
import com.jitterbug.concurrency.queue.BlockingQueueAdapter;
import com.jitterbug.concurrency.queue.EventQueue;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) throws IOException {
        EventQueue queue = new BlockingQueueAdapter(1000);
        EventGenerator generator = new EventGenerator();

        EventAggregator aggregator = new ConcurrentAggregator();
        AggregatingEventHandler handler = new AggregatingEventHandler(aggregator);

        CsvMetricsCollector metricsCollector = new CsvMetricsCollector("metrics.csv");
        MetricsEventHandler metricsEventHandler = new MetricsEventHandler(handler, metricsCollector);

        MetricsScheduler scheduler = new MetricsScheduler();
        scheduler.schedule(metricsCollector, 1000);

        ExecutorEventProcessor processor = new ExecutorEventProcessor(4);
        processor.start(queue, metricsEventHandler);
        generator.eventStream(50000, queue::publish);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException ignore) {

        } finally {
            processor.stop();
            scheduler.shutdown();
            metricsCollector.shutdown();
        }

        System.out.println("Count for user 42 = " + aggregator.getCountForUser(42));
    }
}
