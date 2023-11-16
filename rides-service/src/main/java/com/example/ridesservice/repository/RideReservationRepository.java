package com.example.ridesservice.repository;

import com.example.ridesservice.model.RideReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideReservationRepository extends JpaRepository<RideReservation, Long> {
}
