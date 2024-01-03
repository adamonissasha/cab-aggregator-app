package com.example.driverservice.repository;

import com.example.driverservice.model.DriverRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRatingRepository extends JpaRepository<DriverRating, Long> {
    List<DriverRating> getDriverRatingsByDriverId(Long driverId);
}
