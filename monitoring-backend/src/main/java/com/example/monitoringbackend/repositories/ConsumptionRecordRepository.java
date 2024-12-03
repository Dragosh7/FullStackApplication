package com.example.monitoringbackend.repositories;

import com.example.monitoringbackend.entities.ConsumptionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsumptionRecordRepository extends JpaRepository<ConsumptionRecord, UUID> {

    List<ConsumptionRecord> findByDeviceIdAndTimestampBetween(UUID deviceId, Timestamp start, Timestamp end);

    @Query("SELECT COALESCE(SUM(cr.energy), 0) FROM ConsumptionRecord cr " +
            "WHERE cr.deviceId = :deviceId AND cr.timestamp >= :startTime AND cr.timestamp < :endTime")
    double calculateTotalConsumption(@Param("deviceId") UUID deviceId,
                                     @Param("startTime") Timestamp startTime,
                                     @Param("endTime") Timestamp endTime);

}
