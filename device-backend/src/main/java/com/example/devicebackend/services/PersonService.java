package com.example.devicebackend.services;

import com.example.devicebackend.dtos.PersonChangeDTO;
import com.example.devicebackend.dtos.PersonDTO;
import com.example.devicebackend.dtos.PersonDetailsDTO;
import com.example.devicebackend.dtos.PersonMonitorDTO;
import com.example.devicebackend.dtos.builders.PersonBuilder;
import com.example.devicebackend.entities.Device;
import com.example.devicebackend.entities.Person;
import com.example.devicebackend.repositories.DeviceRepository;
import com.example.devicebackend.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    private final DeviceRepository deviceRepository;

    @Autowired
    public PersonService(PersonRepository personRepository,DeviceRepository deviceRepository) {
        this.personRepository = personRepository;
        this.deviceRepository= deviceRepository;
    }

    public List<PersonDTO> findPersons() {
        List<Person> personList = personRepository.findAll();
        return personList.stream()
                .map(PersonBuilder::toPersonDTO)
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

    public UUID insert(PersonDetailsDTO personDTO) {
        Person person = PersonBuilder.toEntity(personDTO);
        Optional<Person> personOptional = personRepository.findByName(personDTO.getName());
        if (personOptional.isEmpty()) {
            person = personRepository.save(person);
            LOGGER.debug("Person with id {} was inserted in db", person.getId());
            return person.getId();
        }
        LOGGER.debug("Person already exists {} in db", person.getId());
        return null;
    }

    public UUID updatePerson(UUID id, PersonDetailsDTO personDTO) throws Exception {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found", id);
            throw new Exception(Person.class.getSimpleName() + " with id: " + id);
        }
        Person person = personOptional.get();
        person.setName(personDTO.getName());
        personRepository.save(person);
        return person.getId();
    }

    // Delete
    public void deletePerson(UUID id) throws Exception {
        Optional<Person> personOptional = personRepository.findById(id);

        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found", id);
            throw new Exception(Person.class.getSimpleName() + " with id: " + id);
        }

        Person person = personOptional.get();

        List<Device> devices = deviceRepository.findByPersonId(person.getId());

        for (Device device : devices) {
            device.setPerson(null);
            deviceRepository.save(device);
        }

        personRepository.delete(person);

        LOGGER.info("Person with id {} was deleted, and associated devices were unlinked", id);
    }

    //RabbitMQ
    @RabbitListener(queues = "user-device-queue")
    public void receiveUserChange(PersonChangeDTO message) throws Exception {

        System.out.println("Received message: " + message.toString());
        System.out.println("Received message: " + message.getAction());
        switch(message.getAction())
        {
            case ADD -> handleAdd(message.getPerson());

            case DELETE -> handleDelete(message.getPerson());

            case UPDATE -> handleUpdate(message.getPerson());

            default -> System.out.println("not going right: " );
        }
    }

    private void handleAdd(PersonMonitorDTO personDTO) throws Exception {

        Person person = PersonBuilder.personMonitorToEntity(personDTO);
        personRepository.save(person);

        //throw new RuntimeException("Adding device Failed");

    }

    private void handleDelete(PersonMonitorDTO personDTO) throws Exception {

        Optional<Person> person = personRepository.findByName(personDTO.getName());
        if(person.isPresent())
        {
            personRepository.delete(person.get());
        }

        //throw new RuntimeException("Adding device Failed");

    }

    private void handleUpdate(PersonMonitorDTO personDTO) throws Exception {

        Optional<Person> person = personRepository.findById(personDTO.getId());
        if(person.isPresent())
        {
            Person toSave = person.get();
            toSave.setName(personDTO.getName());
            personRepository.save(toSave);
        }

        //throw new RuntimeException("Adding device Failed");

    }
}
