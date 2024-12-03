package com.example.monitoringbackend.services;

import com.example.monitoringbackend.dtos.PersonChangeDTO;
import com.example.monitoringbackend.dtos.PersonMonitorDTO;
import com.example.monitoringbackend.dtos.builders.PersonBuilder;
import com.example.monitoringbackend.entities.Person;
import com.example.monitoringbackend.repositories.PersonRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConsumerPersonService {
    private final PersonRepository personRepository;

    @Autowired
    public ConsumerPersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @RabbitListener(queues = "user-change-queue")
    public void receiveDeviceChange(PersonChangeDTO message) throws Exception {

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

        Person person = PersonBuilder.toEntity(personDTO);
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
