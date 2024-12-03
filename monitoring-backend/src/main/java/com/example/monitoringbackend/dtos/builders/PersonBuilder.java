package com.example.monitoringbackend.dtos.builders;

import com.example.monitoringbackend.dtos.DeviceMonitorDTO;
import com.example.monitoringbackend.dtos.PersonMonitorDTO;
import com.example.monitoringbackend.entities.Device;
import com.example.monitoringbackend.entities.Person;

public class PersonBuilder {

    private PersonBuilder() {
    }


    public static Person toEntity(PersonMonitorDTO personMonitorDTO) {
        return new Person(personMonitorDTO.getId(),
                personMonitorDTO.getName());
    }
}
