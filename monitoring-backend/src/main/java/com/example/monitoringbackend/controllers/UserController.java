package com.example.monitoringbackend.controllers;

import com.example.monitoringbackend.entities.Device;
import com.example.monitoringbackend.entities.Person;
import com.example.monitoringbackend.repositories.PersonRepository;
import com.example.monitoringbackend.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final PersonRepository repo;

    @Autowired
    public UserController(PersonRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/persons")
    public ResponseEntity<List<Person>> getPersons() {
        return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
    }
}
