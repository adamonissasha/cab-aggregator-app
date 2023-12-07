package com.example.driverservice.repository;

import com.example.driverservice.model.Driver;
import com.example.driverservice.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findDriverByPhoneNumber(String phoneNumber);

    Optional<Driver> findFirstByStatus(Status status);
}
