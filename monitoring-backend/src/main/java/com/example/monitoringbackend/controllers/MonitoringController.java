package com.example.monitoringbackend.controllers;

import com.example.monitoringbackend.entities.ConsumptionRecord;
import com.example.monitoringbackend.entities.HourlyMedianConsumption;
import com.example.monitoringbackend.services.MonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/consumption")
public class MonitoringController {

    private final MonitoringService monitoringService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/notify")
    public void notifyUser(@RequestBody ConsumptionRecord record) {
        monitoringService.saveConsumptionRecord(record);

        // Send real-time notification
        messagingTemplate.convertAndSendToUser(
                String.valueOf(record.getDeviceId()), // User identifier (could be userId)
                "/topic/consumption", // WebSocket topic
                record
        );
    }

    @GetMapping("/{deviceId}")
    public List<HourlyMedianConsumption> getConsumptionForDevice(
            @PathVariable UUID deviceId,
            @RequestParam String date // Expected format: YYYY-MM-DD
    ) {
        LocalDate localDate = LocalDate.parse(date);
        return monitoringService.getConsumptionByDeviceAndDated(deviceId, localDate);
    }
}
