package com.jitterbug.concurrency;

import com.jitterbug.concurrency.event.EventGenerator;
import com.jitterbug.concurrency.processing.EventHandler;
import com.jitterbug.concurrency.processing.ExecutorEventProcessor;
import com.jitterbug.concurrency.processing.LoggingEventHandler;
import com.jitterbug.concurrency.queue.BlockingQueueAdapter;
import com.jitterbug.concurrency.queue.EventQueue;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        EventQueue queue = new BlockingQueueAdapter(1000);
        EventGenerator generator = new EventGenerator();

        EventHandler handler = new LoggingEventHandler();

        ExecutorEventProcessor processor = new ExecutorEventProcessor(4);
        processor.start(queue, handler);

        generator.eventStream(10, event -> {
            System.out.println("Generated: " + event);
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignore) {

        } finally {
            processor.stop();
        }


    }
}
