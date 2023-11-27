package com.example.ridesservice.repository;

import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {
    List<Stop> findByRide(Ride ride);
}