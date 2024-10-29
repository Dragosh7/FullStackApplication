package com.example.devicebackend.repositories;

import com.example.devicebackend.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    /**
     * Example: JPA generate Query by Field
     */
    Optional<Person> findByName(String name);
    Optional<Person> findById(UUID uuid);

}
