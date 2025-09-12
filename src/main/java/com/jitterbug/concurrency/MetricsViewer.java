package com.jitterbug.concurrency;

import com.jitterbug.concurrency.visualisation.MetricsChart;

public class MetricsViewer {
    public static void main(String[] args) throws Exception {
        String filePath = "metrics.csv";

        MetricsChart.showCart(filePath, "eventsProcessed", "Events/sec");
        MetricsChart.showCart(filePath, "avgLatencyMs", "Latency (ms)");
        MetricsChart.showCart(filePath, "usedMemoryMB", "Memory (MB)");
    }
}
