package userbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@Entity
public class Person  implements Serializable{


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    public Person() {
    }

    public Person(String name, String address, int age, String password, String role) {
        this.name = name;
        this.address = address;
        this.age = age;
        this.password = password;
        this.role = role;
    }

}
