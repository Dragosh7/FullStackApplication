package userbackend.controllers;

import userbackend.dtos.AuthenticationDTO;
import userbackend.dtos.LoginResponse;
import userbackend.dtos.PersonDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userbackend.services.*;
import userbackend.services.PersonService;

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
    public ResponseEntity<List<PersonDetailsDTO>> getPersonsWithDetails() {
        List<PersonDetailsDTO> dtos = personService.findPersonsWithDetails();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertPerson(@RequestBody PersonDetailsDTO personDTO,
                                             @RequestParam(value = "secretKey", required = false) String secretKey) throws Exception {
        if("parola".equals(secretKey)) {
            UUID personID = personService.insert(personDTO);
            return new ResponseEntity<>(personID, HttpStatus.CREATED);

        }
        else {
            personDTO.setRole("user");
            UUID personID = personService.insert(personDTO);
            return new ResponseEntity<>(personID, HttpStatus.CREATED);

        }

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

    //TODO: UPDATE, DELETE per resource

    @PutMapping(value = "/{name}")
    public ResponseEntity<PersonDetailsDTO> updatePerson(
            @PathVariable("name") String personName,
            @RequestBody PersonDetailsDTO personDetailsDTO) throws Exception {
        PersonDetailsDTO updatedPerson = personService.updatePerson(personName, personDetailsDTO);
        return new ResponseEntity<>(updatedPerson, HttpStatus.OK);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthenticationDTO loginDTO) throws Exception {
        boolean isAuthenticated = personService.authenticateUser(loginDTO.getUsername(), loginDTO.getPassword());
        if (isAuthenticated) {
            LoginResponse toStore = personService.findPersonRights(loginDTO.getUsername());
            toStore.setMessage("Login successful");
            return new ResponseEntity<>(toStore, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new LoginResponse("Login failed"), HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping()
    public ResponseEntity<String> deletePerson(@RequestBody AuthenticationDTO loginDTO) {

        String status = personService.deleteUser(loginDTO.getUsername(), loginDTO.getPassword());

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @DeleteMapping(value="{id}")
    public ResponseEntity<String> deletePerson(@PathVariable("id") UUID personId) {

        String status = personService.deleteUserAsAdmin(personId);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }


}
