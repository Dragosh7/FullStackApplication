package com.example.monitoringbackend.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EnergyMeasurement {
    private final Long timestamp;
    private final String deviceId;
    private final double measurementValue;

    public EnergyMeasurement(Long timestamp, String deviceId, double measurementValue) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.measurementValue = measurementValue;
    }

}
