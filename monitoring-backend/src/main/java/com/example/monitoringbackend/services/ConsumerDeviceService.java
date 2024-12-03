package com.example.monitoringbackend.services;

import com.example.monitoringbackend.dtos.DeviceChangeDTO;
import com.example.monitoringbackend.dtos.DeviceMonitorDTO;
import com.example.monitoringbackend.dtos.builders.DeviceBuilder;
import com.example.monitoringbackend.entities.Device;
import com.example.monitoringbackend.entities.Person;
import com.example.monitoringbackend.repositories.DeviceRepository;
import com.example.monitoringbackend.repositories.PersonRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConsumerDeviceService {
    private final DeviceRepository deviceRepository;
    private final PersonRepository personRepository;

    @Autowired
    public ConsumerDeviceService(DeviceRepository deviceRepository, PersonRepository personRepository) {
        this.deviceRepository = deviceRepository;
        this.personRepository = personRepository;
    }

    @RabbitListener(queues = "device-change-queue")
    public void receiveDeviceChange(DeviceChangeDTO message) throws Exception {

        System.out.println("Received message: " + message.toString());
        System.out.println("Received message: " + message.getDevice().getName());
        System.out.println("Received message: " + message.getAction());
        switch(message.getAction())
        {
            case ADD -> handleAdd(message.getDevice());
            case UPDATE -> handleUpdate(message.getDevice());
            case DELETE -> handleDelete(message.getDevice());

            default -> System.out.println("not going right: " );
        }
    }

    private void handleAdd(DeviceMonitorDTO deviceDTO) throws Exception {

        Device device = DeviceBuilder.toEntity(deviceDTO);
        if (deviceDTO.getPersonName() != null) {
            Optional<Person> person = personRepository.findByName(deviceDTO.getPersonName());
            if (!person.isPresent()) {
                throw new Exception("Person with name: " + deviceDTO.getPersonName() + " not found");
            }
            device.setPerson(person.get());
        }
        device = deviceRepository.save(device);
        System.out.println(("Device with id {} was inserted in db"+ device.getId()));

        //throw new RuntimeException("Adding device Failed");

    }

    private void handleUpdate(DeviceMonitorDTO deviceDTO) throws Exception {

        Optional<Device> device = deviceRepository.findById(deviceDTO.getId());
        if (!device.isPresent()) {
            throw new Exception("Device with id " + deviceDTO.getId() + " not found");
        }
        Device deviceToSave = DeviceBuilder.toEntity(deviceDTO);
        System.out.println(personRepository.findByName(deviceDTO.getPersonName()).get());
        Person person = personRepository.findByName(deviceDTO.getPersonName()).get();
        deviceToSave.setPerson(person);
        deviceRepository.save(deviceToSave);


        //throw new RuntimeException("Adding device Failed");

    }
    private void handleDelete(DeviceMonitorDTO deviceDTO) throws Exception {

        Optional<Device> device = deviceRepository.findById(deviceDTO.getId());
        if (!device.isPresent()) {
            throw new Exception("Device with id " + deviceDTO.getId() + " not found");
        }
        deviceRepository.delete(device.get());


        //throw new RuntimeException("Adding device Failed");

    }
}
