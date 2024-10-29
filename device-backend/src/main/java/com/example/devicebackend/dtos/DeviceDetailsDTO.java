package com.example.devicebackend.dtos;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;


import java.util.Objects;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeviceDetailsDTO {


    private UUID id;
    private String name;
    private String model;
    private String address;
    private int energy;

    private String personName;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDetailsDTO that = (DeviceDetailsDTO) o;
        return energy == that.energy &&
                Objects.equals(name, that.name) &&
                Objects.equals(model, that.model) &&
                Objects.equals(address, that.address);
    }

    public @NotNull UUID getId() {
        return id;
    }

    public void setId(@NotNull UUID id) {
        this.id = id;
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getModel() {
        return model;
    }

    public void setModel(@NotNull String model) {
        this.model = model;
    }

    public @NotNull String getAddress() {
        return address;
    }

    public void setAddress(@NotNull String address) {
        this.address = address;
    }

    @NotNull
    public int getEnergy() {
        return energy;
    }

    public void setEnergy(@NotNull int energy) {
        this.energy = energy;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, model, address, energy);
    }
}
