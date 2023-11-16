package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.PassengerResponse;
import org.springframework.data.domain.Page;

public interface PassengerService {
    PassengerResponse createPassenger(PassengerRequest passengerRequest);

    PassengerResponse editPassenger(long id, PassengerRequest passengerRequest);

    PassengerResponse getPassengerById(long id);

    Page<PassengerResponse> getAllPassengers(int page, int size, String sortBy);
}
