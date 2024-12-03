package com.example.monitoringbackend.entities;

public class HourlyMedianConsumption {
    private int hour;
    private double medianConsumptionValue;

    // Constructor
    public HourlyMedianConsumption(int hour, double medianConsumptionValue) {
        this.hour = hour;
        this.medianConsumptionValue = medianConsumptionValue;
    }

    // Getters and Setters
    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public double getMedianConsumptionValue() {
        return medianConsumptionValue;
    }

    public void setMedianConsumptionValue(double medianConsumptionValue) {
        this.medianConsumptionValue = medianConsumptionValue;
    }
}

