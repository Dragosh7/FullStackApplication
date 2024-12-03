package com.example.monitoringbackend.services;

import com.example.monitoringbackend.entities.ConsumptionRecord;
import com.example.monitoringbackend.entities.Device;
import com.example.monitoringbackend.entities.HourlyMedianConsumption;
import com.example.monitoringbackend.repositories.ConsumptionRecordRepository;
import com.example.monitoringbackend.repositories.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MonitoringService {

    private final DeviceRepository deviceRepository;
    private final ConsumptionRecordRepository repository;

    @Autowired
    public MonitoringService(ConsumptionRecordRepository repository, DeviceRepository deviceRepository) {
        this.repository = repository;
        this.deviceRepository = deviceRepository;
    }
    public List<Device> getDevicesForUser(UUID userId) {
        return deviceRepository.findAll()
                .stream()
                .filter(device -> device.getPerson() != null && device.getPerson().getId().equals(userId))
                .toList();
    }

    public void saveConsumptionRecord(ConsumptionRecord record) {
        repository.save(record);
    }

    /*public List<ConsumptionRecord> getConsumptionByDeviceAndDate(UUID deviceId, LocalDate date) {
        Timestamp startOfDay = Timestamp.valueOf(date.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(date.atTime(23, 59, 59));
        return repository.findByDeviceIdAndTimestampBetween(deviceId, startOfDay, endOfDay);
    }*/
    public List<HourlyMedianConsumption> getConsumptionByDeviceAndDate(UUID deviceId, LocalDate date) {
        // Parse the date
        LocalDate localDate = date;
        // Convert LocalDate to LocalDateTime for start and end of the day
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);

        // Convert LocalDateTime to Timestamp for query
        Timestamp startTimestamp = Timestamp.valueOf(startOfDay);
        Timestamp endTimestamp = Timestamp.valueOf(endOfDay);

        // Fetch records from the repository based on deviceId and timestamp range
        List<ConsumptionRecord> records = repository.findByDeviceIdAndTimestampBetween(deviceId, startTimestamp, endTimestamp);

        // Group records by hour of the timestamp
        Map<Integer, List<ConsumptionRecord>> hourlyRecords = records.stream()
                .collect(Collectors.groupingBy(record -> record.getTimestamp().toLocalDateTime().getHour()));

        // List to hold the result with hourly median consumption
        List<HourlyMedianConsumption> medianHourlyConsumption = new ArrayList<>();

        // For each hour, calculate the median consumption
        for (Map.Entry<Integer, List<ConsumptionRecord>> entry : hourlyRecords.entrySet()) {
            int hour = entry.getKey();
            List<ConsumptionRecord> hourRecords = entry.getValue();

            // Extract energy consumption values and calculate the median
            List<Double> energyValues = hourRecords.stream()
                    .map(ConsumptionRecord::getEnergy) // Assuming energy is the field we need
                    .sorted()
                    .collect(Collectors.toList());

            double median = calculateMedian(energyValues);

            // Add the result to the list
            medianHourlyConsumption.add(new HourlyMedianConsumption(hour, median));
        }

        return medianHourlyConsumption;
    }

    // Helper method to calculate the median
    private double calculateMedian(List<Double> values) {
        int size = values.size();
        if (size == 0) {
            return 0.0; // No data to calculate median
        }
        if (size % 2 == 1) {
            return values.get(size / 2); // Odd size, return the middle element
        } else {
            return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0; // Even size, average the two middle elements
        }
    }

    public List<HourlyMedianConsumption> getConsumptionByDeviceAndDated(UUID deviceId, LocalDate date) {
        // Convert LocalDate to LocalDateTime for start and end of the day
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        // Convert LocalDateTime to Timestamp for query
        Timestamp startTimestamp = Timestamp.valueOf(startOfDay);
        Timestamp endTimestamp = Timestamp.valueOf(endOfDay);

        // Fetch records from the repository based on deviceId and timestamp range
        List<ConsumptionRecord> records = repository.findByDeviceIdAndTimestampBetween(deviceId, startTimestamp, endTimestamp);

        // Group records by hour of the timestamp
        Map<Integer, List<ConsumptionRecord>> hourlyRecords = records.stream()
                .collect(Collectors.groupingBy(record -> record.getTimestamp().toLocalDateTime().getHour()));

        // List to hold the result with hourly energy consumption totals
        List<HourlyMedianConsumption> hourlyEnergyConsumption = new ArrayList<>();

        // For each hour, calculate the total energy consumption
        for (Map.Entry<Integer, List<ConsumptionRecord>> entry : hourlyRecords.entrySet()) {
            int hour = entry.getKey();
            List<ConsumptionRecord> hourRecords = entry.getValue();

            // Calculate the total energy consumption for this hour
            double totalEnergy = hourRecords.stream()
                    .mapToDouble(ConsumptionRecord::getEnergy) // Assuming energy is the field to sum
                    .sum();

            // Add the result to the list
            hourlyEnergyConsumption.add(new HourlyMedianConsumption(hour, totalEnergy));
        }

        return hourlyEnergyConsumption;
    }

}
