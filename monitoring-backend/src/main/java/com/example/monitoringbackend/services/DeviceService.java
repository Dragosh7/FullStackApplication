package com.example.monitoringbackend.services;

import com.example.monitoringbackend.entities.Device;
import com.example.monitoringbackend.repositories.DeviceRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> findAllDevices() {
        return new ArrayList<>(deviceRepository.findAll());
    }
}
