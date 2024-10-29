package com.example.devicebackend.repositories;

import com.example.devicebackend.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    /**
     * Example: JPA generate Query by Field
     */
    List<Device> findByName(String name);


    List<Device> findByPersonId(UUID personId);
    List<Device> findByPersonIsNull(); // Find unlinked devices
}
