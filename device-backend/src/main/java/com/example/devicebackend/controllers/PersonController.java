package com.example.devicebackend.controllers;

import com.example.devicebackend.dtos.PersonDTO;
import com.example.devicebackend.dtos.PersonDetailsDTO;
import com.example.devicebackend.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;


@RestController
@CrossOrigin
@RequestMapping(value = "/person")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping()
    public ResponseEntity<List<PersonDTO>> getPersons() {
        List<PersonDTO> dtos = personService.findPersons();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertPerson(@RequestBody PersonDetailsDTO personDTO) {
        UUID personID = personService.insert(personDTO);
        return new ResponseEntity<>(personID, HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity<UUID> updatePerson(@RequestBody PersonDetailsDTO personDTO) throws Exception {
        UUID personID = personService.updatePerson(personDTO.getId(),personDTO);
        return new ResponseEntity<>(personID, HttpStatus.OK);
    }

    @GetMapping(value = "/id:{id}")
    public ResponseEntity<PersonDetailsDTO> getPerson(@PathVariable("id") UUID personId) throws Exception {
        PersonDetailsDTO dto = personService.findPersonById(personId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(value = "/{name}")
    public ResponseEntity<PersonDetailsDTO> getPerson(@PathVariable("name") String name) throws Exception {
        PersonDetailsDTO dto = personService.findPersonByName(name);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deletePerson(@PathVariable("id") UUID personId) throws Exception {
        personService.deletePerson(personId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
