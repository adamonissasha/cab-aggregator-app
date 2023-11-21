package com.example.ridesservice.repository;

import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findAllByStatus(RideStatus status);
}