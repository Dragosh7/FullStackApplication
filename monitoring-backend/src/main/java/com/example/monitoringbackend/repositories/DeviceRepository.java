package com.example.monitoringbackend.repositories;

import com.example.monitoringbackend.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
}
