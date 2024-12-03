package com.example.monitoringbackend.dtos.builders;

import com.example.monitoringbackend.dtos.DeviceMonitorDTO;
import com.example.monitoringbackend.entities.Device;

public class DeviceBuilder {

    private DeviceBuilder() {
    }


    public static Device toEntity(DeviceMonitorDTO deviceDetailsDTO) {
        return new Device(deviceDetailsDTO.getId(),
                deviceDetailsDTO.getName(),
                deviceDetailsDTO.getEnergy());
    }

}

