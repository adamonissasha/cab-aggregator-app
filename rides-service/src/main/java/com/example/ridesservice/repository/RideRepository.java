package com.example.ridesservice.repository;

import com.example.ridesservice.model.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    Page<Ride> findAllByPassengerId(long passengerId, Pageable pageable);

    Page<Ride> findAllByDriverId(long driverId, Pageable pageable);
}