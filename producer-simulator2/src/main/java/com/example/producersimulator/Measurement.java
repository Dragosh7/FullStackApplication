package com.example.producersimulator;

public class Measurement {
    private long timestamp;
    private String device_id;
    private double measurement_value;

    public Measurement(long timestamp, String device_id, double measurement_value) {
        this.timestamp = timestamp;
        this.device_id = device_id;
        this.measurement_value = measurement_value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDevice_id() {
        return device_id;
    }

    public double getMeasurement_value() {
        return measurement_value;
    }
}

