package com.example.devicebackend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Device implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "energy", nullable = false)
    private Double energy;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "person_id")
    private Person person;

    public Device(String name, String model, Person person, String address, Double energy) {
        this.name = name;
        this.model = model;
        this.person = person;
        this.address = address;
        this.energy = energy;
    }

    public Device(String name, String model, String address, Double energy) {
        this.name = name;
        this.model = model;
        this.address = address;
        this.energy = energy;
    }
}
