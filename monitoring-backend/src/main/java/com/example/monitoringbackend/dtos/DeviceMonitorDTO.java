package com.example.monitoringbackend.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class DeviceMonitorDTO {
    private UUID id;
    private String name;
    private Double energy;
    private String personName;
}
