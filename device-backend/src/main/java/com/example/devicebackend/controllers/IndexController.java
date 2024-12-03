package com.example.devicebackend.controllers;

import com.example.devicebackend.services.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
public class IndexController {

    private final DeviceService deviceService;

    public IndexController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<String> getStatus() {
        deviceService.init();
        return new ResponseEntity<>("City APP Service is running...", HttpStatus.OK);
    }
}
