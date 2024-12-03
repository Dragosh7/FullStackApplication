package com.example.producersimulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


@SpringBootApplication
public class ProducerSimulatorApplication implements CommandLineRunner {

    @Value("${device.id}")
    private String deviceId;

    @Value("${sensor.csv.path}")
    private String csvFilePath;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ProducerSimulatorApplication(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProducerSimulatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (deviceId == null || deviceId.isEmpty()) {
            throw new IllegalArgumentException("Device ID is not configured in application.properties");
        }

        if (!Files.exists(Paths.get(csvFilePath))) {
            throw new IllegalArgumentException("CSV file not found: " + csvFilePath);
        }

        BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
        Timer timer = new Timer();

        System.out.println("Starting Smart Meter Simulator...");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        System.out.println("Reached end of CSV file. Stopping simulator...");
                        timer.cancel();
                        reader.close();
                        return;
                    }
                    long currentTime = System.currentTimeMillis();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTime);

                    calendar.add(Calendar.HOUR, 1);
                    long oneHourLater = calendar.getTimeInMillis();

                    double measurementValue = Double.parseDouble(line.trim());
                    Measurement measurementOneHourLater = new Measurement(oneHourLater, deviceId, measurementValue);
                    calendar.add(Calendar.DATE, 1);
                    long oneDayLater = calendar.getTimeInMillis();
                    Measurement measurementOneDayLater = new Measurement(oneDayLater, deviceId, measurementValue);

                    Measurement measurement = new Measurement(System.currentTimeMillis(), deviceId, measurementValue);

//                    String messageJson = objectMapper.writeValueAsString(measurementOneHourLater);
//                    rabbitTemplate.convertAndSend("energy-measurements", messageJson);
//                     messageJson = objectMapper.writeValueAsString(measurementOneDayLater);
//                    rabbitTemplate.convertAndSend("energy-measurements", messageJson);
                    String messageJson = objectMapper.writeValueAsString(measurementOneHourLater);
                    rabbitTemplate.convertAndSend("energy-measurements", messageJson);

                    System.out.println("Sent: " + messageJson);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1 * 15 * 1000); // 15 sec
    }
}
