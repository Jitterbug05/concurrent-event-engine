package com.jitterbug.concurrency;

import com.jitterbug.concurrency.event.EventGenerator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        EventGenerator generator = new EventGenerator();

        generator.eventStream(10, event -> {
            System.out.println("Generated: " + event);
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignore) {}
    }
}
