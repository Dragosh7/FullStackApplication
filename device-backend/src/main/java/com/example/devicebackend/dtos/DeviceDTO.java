package com.example.devicebackend.dtos;

import lombok.*;


import java.util.Objects;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DeviceDTO  {
    private UUID id;
    private String name;
    private String model;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO deviceDTO = (DeviceDTO) o;
        return Objects.equals(name, deviceDTO.name) &&
                Objects.equals(model, deviceDTO.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, model);
    }
}
