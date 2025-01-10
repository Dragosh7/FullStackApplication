package userbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userbackend.services.PersonService;


@RestController
@CrossOrigin
public class IndexController {

    private final PersonService personService;

    public IndexController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping(value = "/init")
    public ResponseEntity<String> getStatus() {
        personService.init();
        return new ResponseEntity<>("City APP Service is running...", HttpStatus.OK);
    }
}
