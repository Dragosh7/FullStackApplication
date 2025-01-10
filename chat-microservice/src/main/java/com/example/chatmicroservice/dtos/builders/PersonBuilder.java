package com.example.chatmicroservice.dtos.builders;

import com.example.chatmicroservice.dtos.PersonMonitorDTO;
import com.example.chatmicroservice.entities.Person;

public class PersonBuilder {

    private PersonBuilder() {
    }


    public static Person toEntity(PersonMonitorDTO personMonitorDTO) {
        return new Person(personMonitorDTO.getId(),
                personMonitorDTO.getName());
    }
}
