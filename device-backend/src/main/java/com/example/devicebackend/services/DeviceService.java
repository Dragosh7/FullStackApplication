package com.example.devicebackend.services;

import com.example.devicebackend.dtos.DeviceChangeDTO;
import com.example.devicebackend.dtos.DeviceDTO;
import com.example.devicebackend.dtos.DeviceDetailsDTO;
import com.example.devicebackend.dtos.builders.DeviceBuilder;
import com.example.devicebackend.entities.ActionType;
import com.example.devicebackend.entities.Device;
import com.example.devicebackend.entities.Person;
import com.example.devicebackend.repositories.DeviceRepository;
import com.example.devicebackend.repositories.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;
    private final PersonRepository personRepository;
    private final RabbitTemplate rabbitTemplate;


    @Autowired
    public DeviceService(DeviceRepository deviceRepository, PersonRepository personRepository, RabbitTemplate rabbitTemplate) {
        this.deviceRepository = deviceRepository;
        this.personRepository = personRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    // Create
    public UUID insert(DeviceDetailsDTO deviceDTO) throws Exception {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        if (deviceDTO.getPersonName() != null) {
            Optional<Person> person = personRepository.findByName(deviceDTO.getPersonName());
            if (!person.isPresent()) {
                throw new Exception("Person with name: " + deviceDTO.getPersonName() + " not found");
            }
            device.setPerson(person.get());
        }
        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());


        rabbitTemplate.convertAndSend( "device-change-queue", new DeviceChangeDTO(DeviceBuilder.toDeviceMonitorDTO(device),
                ActionType.ADD));
        System.out.println("Sent message: " + new ObjectMapper().writeValueAsString(new DeviceChangeDTO(DeviceBuilder.toDeviceMonitorDTO(device), ActionType.ADD)));
        return device.getId();
    }

    // Read all
    public List<DeviceDTO> findAllDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    // Read by ID
    public DeviceDetailsDTO findDeviceById(UUID id) throws Exception {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found", id);
            throw new Exception(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDetailsDTO(deviceOptional.get());
    }

    // Update
    public UUID updateDevice(UUID id, DeviceDetailsDTO deviceDTO) throws Exception {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found", id);
            throw new Exception(Device.class.getSimpleName() + " with id: " + id);
        }
        Device device = deviceOptional.get();
        device.setName(deviceDTO.getName());
        device.setModel(deviceDTO.getModel());
        device.setAddress(deviceDTO.getAddress());
        device.setEnergy(deviceDTO.getEnergy());

        if(deviceDTO.getPersonName().equals("null")) {
            device.setPerson(null);
        }
        else if (deviceDTO.getPersonName() != null) {
            Optional<Person> person = personRepository.findByName(deviceDTO.getPersonName());
            if (!person.isPresent()) {
                throw new Exception("Person with id: " + deviceDTO.getPersonName() + " not found");
            }
            device.setPerson(person.get());
        }
        deviceRepository.save(device);
        rabbitTemplate.convertAndSend( "device-change-queue", new DeviceChangeDTO(DeviceBuilder.toDeviceMonitorDTO(device),
                ActionType.UPDATE));
        return device.getId();
    }

    public void deleteDevice(UUID id) throws Exception {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found", id);
            throw new Exception(Device.class.getSimpleName() + " with id: " + id);
        }
        Device device = deviceOptional.get();
        DeviceDetailsDTO deviceDTO = DeviceBuilder.toDeviceDetailsDTO(device);
        if (device.getPerson() != null) {
            device.setPerson(null); // This unlinks the device from the user
            deviceRepository.save(device);
        }

        deviceRepository.delete(device);
        rabbitTemplate.convertAndSend( "device-change-queue", new DeviceChangeDTO(DeviceBuilder.toDeviceMonitorDTO(device),
                ActionType.DELETE));
    }

    public List<DeviceDetailsDTO> findDevices() {
        List<Device> devicesList = deviceRepository.findAll();
        return devicesList.stream()
                .map(DeviceBuilder::toDeviceDetailsDTO)
                .collect(Collectors.toList());
    }

    public List<DeviceDetailsDTO> findDevicesByPersonName(String personName) {
        Optional<Person> person = personRepository.findByName(personName);
        if (person.isPresent()) {
            List<Device> devicesList = deviceRepository.findByPersonId(person.get().getId());
            return devicesList.stream()
                    .map(DeviceBuilder::toDeviceDetailsDTO)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<DeviceDetailsDTO> findUnlinkedDevices() {
        List<Device> device = deviceRepository.findByPersonIsNull();
        if (!device.isEmpty()) {
            return device.stream()
                    .map(DeviceBuilder::toDeviceDetailsDTO)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public void linkDevice(UUID deviceId, String personName) {
        System.out.println("here");
        Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));
        System.out.println(device);
        System.out.println(personName);
        Optional<Person> person = personRepository.findByName(personName); // Find person by name
        System.out.println(person.get());
        if (person.isPresent()) {
            Person person1 = person.get();
            device.setPerson(person1);
            DeviceDetailsDTO deviceDTO = DeviceBuilder.toDeviceDetailsDTO(device);
            deviceRepository.save(device);
            rabbitTemplate.convertAndSend( "device-change-queue", new DeviceChangeDTO(DeviceBuilder.toDeviceMonitorDTO(device),
                    ActionType.UPDATE));

        }
    }

    public void unlinkDevice(UUID deviceId) throws Exception {
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        if (!deviceOptional.isPresent()) {
            throw new Exception("Device not found with id: " + deviceId);
        }

        Device device = deviceOptional.get();
        device.setPerson(null);
        DeviceDetailsDTO deviceDTO = DeviceBuilder.toDeviceDetailsDTO(device);
        deviceRepository.save(device);
        rabbitTemplate.convertAndSend( "device-change-queue", new DeviceChangeDTO(DeviceBuilder.toDeviceMonitorDTO(device),
                ActionType.UPDATE));
        }

    public void init() {
        // Check if a specific device already exists
        List<Device> existingDevice = deviceRepository.findByName("Smart Thermostat");
        if (existingDevice.isEmpty()) {

            Device device1 = new Device(
                    "Smart Thermostat",
                    "ThermoX200",
                    "Living Room",
                    150.0
            );

            Device device2 = new Device(
                    "Security Camera",
                    "CamSecure500",
                    "Front Door",
                    50.0
            );

            Device device3 = new Device(
                    "Smart Bulb",
                    "Light A19",
                    "Bedroom",
                    20.0
            );

            deviceRepository.save(device1);
            deviceRepository.save(device2);
            deviceRepository.save(device3);
            rabbitTemplate.convertAndSend( "device-change-queue", new DeviceChangeDTO(DeviceBuilder.toDeviceMonitorDTO(device1),
                    ActionType.ADD));
            rabbitTemplate.convertAndSend( "device-change-queue", new DeviceChangeDTO(DeviceBuilder.toDeviceMonitorDTO(device2),
                    ActionType.ADD));
            rabbitTemplate.convertAndSend( "device-change-queue", new DeviceChangeDTO(DeviceBuilder.toDeviceMonitorDTO(device3),
                    ActionType.ADD));
        }
    }

}
