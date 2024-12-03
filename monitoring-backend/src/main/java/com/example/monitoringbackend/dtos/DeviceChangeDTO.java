package com.example.monitoringbackend.dtos;

import com.example.monitoringbackend.entities.ActionType;
import com.example.monitoringbackend.entities.Device;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class DeviceChangeDTO implements Serializable {
    private DeviceMonitorDTO device;
    private ActionType action;
}
