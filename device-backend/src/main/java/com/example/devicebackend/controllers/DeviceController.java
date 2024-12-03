package com.example.devicebackend.controllers;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.devicebackend.dtos.DeviceDTO;
import com.example.devicebackend.dtos.DeviceDetailsDTO;
import com.example.devicebackend.dtos.DeviceLinkRequest;
import com.example.devicebackend.services.DeviceService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping(value = "/device")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping()
    public ResponseEntity<List<DeviceDetailsDTO>> getDevices() {
        List<DeviceDetailsDTO> dtos = deviceService.findDevices();

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value="/person/{personName}")
    public ResponseEntity<List<DeviceDetailsDTO>> getUserDevices(@PathVariable("personName") String personName) {
        List<DeviceDetailsDTO> dtos = deviceService.findDevicesByPersonName(personName);
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertDevice(@RequestBody DeviceDetailsDTO deviceDTO) throws Exception {
        UUID deviceID = deviceService.insert(deviceDTO);
        return new ResponseEntity<>(deviceID, HttpStatus.CREATED);
    }

    @PutMapping(value="/{id}")
    public ResponseEntity<UUID> updateDevice(@PathVariable("id") UUID deviceId, @RequestBody DeviceDetailsDTO deviceDTO) throws Exception {
        UUID deviceID = deviceService.updateDevice(deviceId, deviceDTO);
        return new ResponseEntity<>(deviceID, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@PathVariable("id") UUID deviceId) throws Exception {
        DeviceDetailsDTO dto = deviceService.findDeviceById(deviceId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable("id") UUID deviceId) throws Exception {
        deviceService.deleteDevice(deviceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/unlink/{deviceId}")
    public ResponseEntity<?> unlinkDevice(@PathVariable("deviceId") UUID deviceId) {
        try {
            deviceService.unlinkDevice(deviceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/unlink")
    public ResponseEntity<?> unlinkedDevices() {
        List<DeviceDetailsDTO> dtos = deviceService.findUnlinkedDevices();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping("/link/{deviceId}")
    public ResponseEntity<?> linkDevice(@PathVariable("deviceId") UUID deviceId, @RequestBody DeviceLinkRequest person) {
        deviceService.linkDevice(deviceId, person.getPersonName());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
