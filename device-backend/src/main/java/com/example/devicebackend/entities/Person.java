package com.example.devicebackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person  implements Serializable{

    @Id
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "person",fetch = FetchType.EAGER)
    private List<Device> devices;

    public Person(String name) {
        this.name = name;
    }

    public Person(UUID id, String name) {
        this.id=id;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
