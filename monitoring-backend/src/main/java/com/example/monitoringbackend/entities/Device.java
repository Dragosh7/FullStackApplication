package com.example.monitoringbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Device {
    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "energy", nullable = false)
    private Double energy;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "person_id")
    private Person person;

    public Device(UUID id, String name, Double energy) {
        this.id = id;
        this.name = name;
        this.energy = energy;
    }
}
