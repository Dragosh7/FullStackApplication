package com.example.monitoringbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person  implements Serializable {

    @Id
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "person", fetch = FetchType.EAGER)
    private List<Device> devices;

    public Person(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
