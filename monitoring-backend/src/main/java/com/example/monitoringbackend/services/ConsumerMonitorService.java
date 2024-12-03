package com.example.monitoringbackend.services;

import com.example.monitoringbackend.entities.ConsumptionRecord;
import com.example.monitoringbackend.entities.Device;
import com.example.monitoringbackend.entities.EnergyMeasurement;
import com.example.monitoringbackend.repositories.ConsumptionRecordRepository;
import com.example.monitoringbackend.repositories.DeviceRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConsumerMonitorService {

    private final ConsumptionRecordRepository consumptionRecordRepository;
    private final DeviceRepository deviceRepository;
    private final WebSocketNotificationService webSocketNotificationService;

    @Autowired
    public ConsumerMonitorService(ConsumptionRecordRepository consumptionRecordRepository,
                                  DeviceRepository deviceRepository,
                                  WebSocketNotificationService webSocketNotificationService) {
        this.consumptionRecordRepository = consumptionRecordRepository;
        this.deviceRepository = deviceRepository;
        this.webSocketNotificationService = webSocketNotificationService;
    }

    @RabbitListener(queues = "energy-measurements")
    public void receiveEnergyMeasurement(String message) {
        System.out.println("Received message: " + message);

        try {
            EnergyMeasurement measurement = parseMeasurement(message);
            processMeasurement(measurement);
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    private EnergyMeasurement parseMeasurement(String message) {
        // Parse the JSON message
        String[] parts = message.replace("{", "").replace("}", "").replace("\"", "").split(",");
        Long timestamp = Long.parseLong(parts[0].split(":")[1].trim());
        String deviceId = parts[1].split(":")[1].trim();
        double measurementValue = Double.parseDouble(parts[2].split(":")[1].trim());

        return new EnergyMeasurement(timestamp, deviceId, measurementValue);
    }

    private void processMeasurement(EnergyMeasurement measurement) {
        UUID deviceId = UUID.fromString(measurement.getDeviceId());
        Optional<Device> deviceOpt = deviceRepository.findById(deviceId);

        if (deviceOpt.isEmpty()) {
            System.out.println("Device not found for ID: " + deviceId);
            return;
        }

        Device device = deviceOpt.get();

        // Convert timestamp to LocalDateTime in UTC (from producer)
        LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(measurement.getTimestamp()), ZoneOffset.UTC);

        // Get current time in your local time zone (adjust to your time zone)
        LocalDateTime currentLocalTime = LocalDateTime.now();

        // Round to the start of the current hour in your local time zone
        LocalDateTime hourStart = currentLocalTime.withMinute(0).withSecond(0).withNano(0);
        Timestamp hourStartTs = Timestamp.valueOf(hourStart);

        // Save the new reading to the database
        ConsumptionRecord newRecord = ConsumptionRecord.builder()
                .deviceId(deviceId)
                .deviceName(device.getName())
                .timestamp(new Timestamp(measurement.getTimestamp()))  // Ensure UTC timestamp saved
                .energy(measurement.getMeasurementValue())
                .limitExceeded(false)  // Placeholder, updated after aggregation
                .build();

        consumptionRecordRepository.save(newRecord);
        System.out.println("Saved new reading: " + newRecord);

        // Calculate the total consumption for the current hour in your local time zone
        double totalConsumption = consumptionRecordRepository
                .calculateTotalConsumption(deviceId, hourStartTs, Timestamp.valueOf(hourStart.plusHours(1)));
        System.out.println("\nTotal Consumption: " + totalConsumption + "\n");
        System.out.println("Calculating total consumption for:");
        System.out.println("Device ID: " + deviceId);
        System.out.println("Start Time: " + hourStartTs);
        System.out.println("End Time: " + Timestamp.valueOf(hourStart.plusHours(1)));

        // Check if the consumption exceeds the limit
        boolean limitExceeded = totalConsumption > device.getEnergy();

        // Update the limitExceeded field for the new record
        newRecord.setLimitExceeded(limitExceeded);
        consumptionRecordRepository.save(newRecord);

        if (limitExceeded) {
            System.out.println("Limit exceeded for device: " + device.getName());
            String username = device.getPerson() != null ? device.getPerson().getName() : "Unknown User";
            webSocketNotificationService.sendLimitExceededNotification(username, device.getName());

        }
    }


}
