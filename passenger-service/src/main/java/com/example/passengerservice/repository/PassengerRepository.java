package com.example.passengerservice.repository;

import com.example.passengerservice.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findPassengerByPhoneNumber(String phoneNumber);
    Optional<Passenger> findPassengerByPhoneNumberAndIdIsNot(String phoneNumber, long id);

}
