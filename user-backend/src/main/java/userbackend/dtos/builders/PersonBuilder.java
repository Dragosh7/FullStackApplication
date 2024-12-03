package userbackend.dtos.builders;

import userbackend.dtos.PersonDTO;
import userbackend.dtos.PersonDetailsDTO;
import userbackend.dtos.PersonDeviceDTO;
import userbackend.dtos.PersonMonitorDTO;
import userbackend.entities.Person;

public class PersonBuilder {

    private PersonBuilder() {
    }

    public static PersonDTO toPersonDTO(Person person) {
        return new PersonDTO(person.getId(), person.getName(), person.getAge());
    }

    public static PersonDeviceDTO toDeviceDB(Person person) {
        return new PersonDeviceDTO(person.getId(), person.getName());
    }

    public static PersonDetailsDTO toPersonDetailsDTO(Person person) {
        return new PersonDetailsDTO(person.getId(), person.getName(), person.getAddress(), person.getAge(), person.getRole());
    }

    public static PersonMonitorDTO toPersonMonitorDTO(Person person) {
        return new PersonMonitorDTO(person.getId(), person.getName());
    }

    public static Person toEntity(PersonDetailsDTO personDetailsDTO) {
        return new Person(personDetailsDTO.getName(),
                personDetailsDTO.getAddress(),
                personDetailsDTO.getAge(),
                personDetailsDTO.getPassword(),
                personDetailsDTO.getRole()
                );
    }
}
