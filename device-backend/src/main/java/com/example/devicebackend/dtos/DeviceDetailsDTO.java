package com.example.devicebackend.dtos;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;


import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeviceDetailsDTO implements Serializable {


    private UUID id;
    private String name;
    private String model;
    private String address;
    private Double energy;

    private String personName;

}
