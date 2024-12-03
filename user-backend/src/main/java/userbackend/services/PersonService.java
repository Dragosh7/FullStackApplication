package userbackend.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import userbackend.dtos.*;
import userbackend.dtos.builders.PersonBuilder;
import userbackend.entities.Person;
import userbackend.repositories.PersonRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final RabbitTemplate rabbitTemplate;


    @Autowired
    public PersonService(PersonRepository personRepository, RabbitTemplate rabbitTemplate) {
        this.personRepository = personRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt()); // Generates a salted hash
    }

    public boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword); // Verifies the password against the stored hash
    }

    public List<PersonDTO> findPersons() {
        List<Person> personList = personRepository.findAll();
        return personList.stream()
                .map(PersonBuilder::toPersonDTO)
                .collect(Collectors.toList());
    }

    public List<PersonDetailsDTO> findPersonsWithDetails() {
        List<Person> personList = personRepository.findAll();
        return personList.stream()
                .map(PersonBuilder::toPersonDetailsDTO)
                .collect(Collectors.toList());
    }

    public PersonDetailsDTO findPersonById(UUID id) throws Exception {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new Exception(Person.class.getSimpleName() + " with id: " + id);
        }
        return PersonBuilder.toPersonDetailsDTO(personOptional.get());
    }

    public PersonDetailsDTO findPersonByName(String name) throws Exception {
        Optional<Person> personOptional = personRepository.findByName(name);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with name {} was not found in db", name);
            throw new Exception(Person.class.getSimpleName() + " with name: " + name);
        }
        return PersonBuilder.toPersonDetailsDTO(personOptional.get());
    }

    public LoginResponse findPersonRights(String name) throws Exception {
        Optional<Person> personOptional = personRepository.findByName(name);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with name {} was not found in db", name);
            throw new Exception(Person.class.getSimpleName() + " with name: " + name);
        }
        return new LoginResponse("",personOptional.get().getId(), personOptional.get().getName(),personOptional.get().getRole());
    }

    public UUID insert(PersonDetailsDTO personDTO) throws Exception {
//        Person person = PersonBuilder.toEntity(personDTO);
//        person = personRepository.save(person);
//        LOGGER.debug("Person with id {} was inserted in db", person.getId());
//        return person.getId();
        Optional<Person> existingPerson = personRepository.findByName(personDTO.getName());
        if (existingPerson.isPresent()) {
            LOGGER.error("Person with name {} already exists", personDTO.getName());
            throw new Exception("Person with name " + personDTO.getName() + " already exists");
        }
        String hashedPassword = hashPassword(personDTO.getPassword());
        personDTO.setPassword(hashedPassword);

        Person person = PersonBuilder.toEntity(personDTO);

        person = personRepository.save(person);
        sendUserToExternalService(PersonBuilder.toDeviceDB(person), "POST");
        rabbitTemplate.convertAndSend( "user-change-queue", new PersonChangeDTO(PersonBuilder.toPersonMonitorDTO(person),
                "ADD"));

        LOGGER.debug("Person with id {} was inserted in db", person.getId());
        return person.getId();
    }

    public PersonDetailsDTO updatePerson(String oldName, PersonDetailsDTO personDTO) throws Exception {

        Optional<Person> personOptionalByOldName = personRepository.findByName(oldName);

        if (!personOptionalByOldName.isPresent()) {
            LOGGER.error("Person with name {} not found in db", oldName);
            throw new Exception("Person with name " + oldName + " not found");
        }

        Person personByOldName = personOptionalByOldName.get();

        if (!oldName.equals(personDTO.getName())) {
            Optional<Person> optionalPersonByCurrentName = personRepository.findByName(personDTO.getName());
            if (optionalPersonByCurrentName.isPresent()) {
                Person personByCurrentName = optionalPersonByCurrentName.get();

                if(!personByCurrentName.getId().equals(personByOldName.getId())) {
                    LOGGER.error("Person with name {} already exists in the db", personDTO.getName());
                    throw new Exception("Person with name " + personDTO.getName() + " already exists");
                }
            }
        }

        Person person = PersonBuilder.toEntity(personDTO);
        person.setId(personByOldName.getId());
        if(!"null".equals(personDTO.getRole())){
            System.out.println(personDTO.getRole());
            person.setRole(personDTO.getRole());
        }
        else{person.setRole(personByOldName.getRole());}
        if(!"null".equals(personDTO.getPassword())){
            String hashedPassword = hashPassword(personDTO.getPassword());
            person.setPassword(hashedPassword);
        }
        else{
            person.setPassword(personByOldName.getPassword());
        }
        person = personRepository.save(person);
        System.out.println(PersonBuilder.toDeviceDB(person));
        sendUserToExternalService(PersonBuilder.toDeviceDB(person),"PUT");
        rabbitTemplate.convertAndSend( "user-change-queue", new PersonChangeDTO(PersonBuilder.toPersonMonitorDTO(person),
                "UPDATE"));

        LOGGER.debug("Person with id {} was updated in db", person.getId());

        return PersonBuilder.toPersonDetailsDTO(person);
    }

    public boolean authenticateUser(String username, String rawPassword) {
        Optional<Person> personOptional = personRepository.findByName(username);

        if (personOptional.isPresent()) {
            String storedHashedPassword = personOptional.get().getPassword();

            return checkPassword(rawPassword, storedHashedPassword);
        }

        return false;
    }

    public String deleteUser(String username, String password) {
        Optional<Person> personOptional = personRepository.findByName(username);

        if (personOptional.isPresent()) {
            if(checkPassword(password, personOptional.get().getPassword())){
                deleteUserFromExternalService(personOptional.get().getId());
                personRepository.delete(personOptional.get());
                rabbitTemplate.convertAndSend( "user-change-queue", new PersonChangeDTO(PersonBuilder.toPersonMonitorDTO(personOptional.get()),
                        "DELETE"));
                return "Success";
            }
            return "Wrong password";
        }

        return "No such user";
    }

    public String deleteUserAsAdmin(UUID id) {
        Optional<Person> personOptional = personRepository.findById(id);

        if (personOptional.isPresent()) {
                deleteUserFromExternalService(personOptional.get().getId());
                rabbitTemplate.convertAndSend( "user-change-queue", new PersonChangeDTO(PersonBuilder.toPersonMonitorDTO(personOptional.get()),
                    "DELETE"));
                personRepository.delete(personOptional.get());

                return "Success";
        }

        return "No such user";
    }

    private void sendUserToExternalService(PersonDeviceDTO personDTO, String method) {
        //String url = "http://localhost:8081/device-backend/person";
        String url = "http://traefik/device-backend/person";

        if ("POST".equalsIgnoreCase(method)) {
            restTemplate.postForEntity(url, personDTO, String.class);
        } else if ("PUT".equalsIgnoreCase(method)) {
            restTemplate.put(url, personDTO);
        }
    }

    private void deleteUserFromExternalService(UUID id) {
        //String url = "http://localhost:8081/device-backend/person/" + id;
        //String url = "http://device-backend:8081/person/" + id;
        String url = "http://traefik/device-backend/person/" + id;
        restTemplate.delete(url);
    }

    public void init() {
        Optional<Person> person = personRepository.findByName("admin");
        if (person.isEmpty()) {
            Person admin = new Person();
            admin.setName("admin");
            admin.setAddress("Default Address");
            admin.setAge(30);
            admin.setPassword(hashPassword("admin"));
            admin.setRole("admin");

            admin = personRepository.save(admin);
            sendUserToExternalService(PersonBuilder.toDeviceDB(admin), "POST");

            rabbitTemplate.convertAndSend( "user-change-queue", new PersonChangeDTO(PersonBuilder.toPersonMonitorDTO(admin),
                    "ADD"));


        }
    }


}
