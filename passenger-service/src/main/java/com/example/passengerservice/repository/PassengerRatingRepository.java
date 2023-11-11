package com.example.passengerservice.repository;

import com.example.passengerservice.model.PassengerRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRatingRepository extends JpaRepository<PassengerRating, Long> {
    List<PassengerRating> getPassengerRatingsByPassengerId(long id);
}
