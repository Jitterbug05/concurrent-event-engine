package com.jitterbug.concurrency.visualisation;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CsvMetricsLoader {

    public static TimeSeriesCollection loadDataset(String filePath, String column) throws IOException {
        TimeSeries series = new TimeSeries(column);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            String line;

            while((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Instant timestamp = Instant.parse(parts[0]);
                ZonedDateTime zonedDateTime = timestamp.atZone(ZoneId.systemDefault());

                long value = Math.round(Double.parseDouble(getColumn(parts, header, column)));
                series.addOrUpdate(new Second(zonedDateTime.getSecond(), zonedDateTime.getMinute(), zonedDateTime.getHour(),
                                              zonedDateTime.getDayOfMonth(), zonedDateTime.getMonthValue(), zonedDateTime.getYear()),
                        value);
            }
        }

        return new TimeSeriesCollection(series);
    }

    private static String getColumn(String[] parts, String header, String column) {
        String[] headers = header.split(",");

        for (int i=0; i<headers.length; i++) {
            if (headers[i].equals(column)) {
                return parts[i];
            }
        }

        throw new IllegalArgumentException("Column not found: " + column);
    }
}
