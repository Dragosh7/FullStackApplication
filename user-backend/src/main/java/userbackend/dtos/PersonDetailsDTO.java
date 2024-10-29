package userbackend.dtos;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
public class PersonDetailsDTO {

    private UUID id;

    private String name;
    private String address;
    private int age;
    private String password;
    private String role;

    public PersonDetailsDTO() {
    }

    public PersonDetailsDTO(String name, String address, int age) {
        this.name = name;
        this.address = address;
        this.age = age;
    }

    public PersonDetailsDTO(UUID id, String name, String address, int age, String role) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.age = age;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDetailsDTO that = (PersonDetailsDTO) o;
        return age == that.age &&
                Objects.equals(name, that.name) &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, age);
    }


}
