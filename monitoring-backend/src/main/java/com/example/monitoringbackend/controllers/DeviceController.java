package com.example.monitoringbackend.controllers;

import com.example.monitoringbackend.entities.Device;
import com.example.monitoringbackend.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping()
    public ResponseEntity<List<Device>> getDevices() {
        return new ResponseEntity<>(deviceService.findAllDevices(), HttpStatus.OK);
    }
}
