package com.example.devicebackend.services;

import com.example.devicebackend.dtos.DeviceDTO;
import com.example.devicebackend.dtos.DeviceDetailsDTO;
import com.example.devicebackend.dtos.builders.DeviceBuilder;
import com.example.devicebackend.entities.Device;
import com.example.devicebackend.entities.Person;
import com.example.devicebackend.repositories.DeviceRepository;
import com.example.devicebackend.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, PersonRepository personRepository) {
        this.deviceRepository = deviceRepository;
        this.personRepository = personRepository;
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
        return device.getId();
    }

    public void deleteDevice(UUID id) throws Exception {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found", id);
            throw new Exception(Device.class.getSimpleName() + " with id: " + id);
        }
        Device device = deviceOptional.get();
        if (device.getPerson() != null) {
            device.setPerson(null); // This unlinks the device from the user
            // Optionally, update the device back to the repository
            deviceRepository.save(device);
        }

        // Finally, delete the device
        deviceRepository.delete(device);
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
            deviceRepository.save(device);

        }
    }

    public void unlinkDevice(UUID deviceId) throws Exception {
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        if (!deviceOptional.isPresent()) {
            throw new Exception("Device not found with id: " + deviceId);
        }

        Device device = deviceOptional.get();
        device.setPerson(null);
        deviceRepository.save(device);
    }

}
