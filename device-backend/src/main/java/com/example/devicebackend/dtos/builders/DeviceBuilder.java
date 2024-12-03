package com.example.devicebackend.dtos.builders;


import com.example.devicebackend.dtos.DeviceDTO;
import com.example.devicebackend.dtos.DeviceDetailsDTO;
import com.example.devicebackend.dtos.DeviceMonitorDTO;
import com.example.devicebackend.entities.Device;

public class DeviceBuilder {

    private DeviceBuilder() {
    }

    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(device.getId(), device.getName(), device.getModel());
    }

    public static DeviceDetailsDTO toDeviceDetailsDTO(Device device) {
        return DeviceDetailsDTO.builder()
                .id(device.getId())
                .name(device.getName())
                .model(device.getModel())
                .address(device.getAddress())
                .energy(device.getEnergy())
                .personName(device.getPerson() != null ? device.getPerson().getName() : "free")
                .build();
    }

    public static DeviceMonitorDTO toDeviceMonitorDTO(Device device) {
        return DeviceMonitorDTO.builder()
                .id(device.getId())
                .name(device.getName())
                .energy(device.getEnergy())
                .personName(device.getPerson() != null ? device.getPerson().getName() : null)
                .build();
    }

    public static DeviceDetailsDTO toDeviceChangeDTO(Device device) {
        return DeviceDetailsDTO.builder()
                .id(device.getId())
                .name(device.getName())
                .model(device.getModel())
                .address(device.getAddress())
                .energy(device.getEnergy())
                .personName(device.getPerson() != null ? device.getPerson().getName() : "free")
                .build();
    }

    public static Device toEntity(DeviceDetailsDTO deviceDetailsDTO) {
        return Device.builder()
                .id(deviceDetailsDTO.getId())
                .name(deviceDetailsDTO.getName())
                .model(deviceDetailsDTO.getModel())
                .address(deviceDetailsDTO.getAddress())
                .energy(deviceDetailsDTO.getEnergy())
                .build();
    }
}
