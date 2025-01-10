package com.example.chatmicroservice.repositories;

import com.example.chatmicroservice.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {
    Optional<Person> findByName(String name);

    @Query("SELECT p.name FROM Person p")
    List<String> findAllUsernames();
}

