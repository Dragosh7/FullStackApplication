package com.example.devicebackend.dtos.builders;

import com.example.devicebackend.dtos.PersonDTO;
import com.example.devicebackend.dtos.PersonDetailsDTO;
import com.example.devicebackend.entities.Person;

public class PersonBuilder {

    private PersonBuilder() {
    }

    public static PersonDTO toPersonDTO(Person person) {
        return new PersonDTO(person.getId(), person.getName());
    }

    public static PersonDetailsDTO toPersonDetailsDTO(Person person) {
        return new PersonDetailsDTO(person.getId(), person.getName());
    }

    public static Person toEntity(PersonDetailsDTO personDetailsDTO) {
        return new Person(personDetailsDTO.getId(), personDetailsDTO.getName());
    }
}
