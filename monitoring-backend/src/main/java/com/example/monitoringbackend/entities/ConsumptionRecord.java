package com.example.monitoringbackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class ConsumptionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID deviceId;
    private String deviceName;
    private Timestamp timestamp;
    private Double energy;
    private boolean limitExceeded;
}
