package com.example.devicebackend.dtos;

import com.example.devicebackend.entities.ActionType;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceChangeDTO implements Serializable {
    private DeviceMonitorDTO device;
    private ActionType action;
}
