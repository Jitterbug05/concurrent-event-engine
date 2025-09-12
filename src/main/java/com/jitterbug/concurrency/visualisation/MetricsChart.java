package com.jitterbug.concurrency.visualisation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MetricsChart {

    public static void showCart(String filePath, String metricName, String yAxisLabel) throws IOException {
        TimeSeriesCollection dataset = CsvMetricsLoader.loadDataset(filePath, metricName);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                metricName + "Over Time",
                "Time",
                yAxisLabel,
                dataset,
                true,
                true,
                false
        );

        JFrame frame = new JFrame(metricName + " Chart");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new ChartPanel(chart), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
