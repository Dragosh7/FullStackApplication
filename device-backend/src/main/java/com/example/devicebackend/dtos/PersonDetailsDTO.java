package com.example.devicebackend.dtos;


import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
public class PersonDetailsDTO {

    private UUID id;
    private String name;

    public PersonDetailsDTO() {
    }

    public PersonDetailsDTO( String name) {
        this.name = name;
    }

    public PersonDetailsDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDetailsDTO that = (PersonDetailsDTO) o;
        return
                Objects.equals(name, that.name) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
