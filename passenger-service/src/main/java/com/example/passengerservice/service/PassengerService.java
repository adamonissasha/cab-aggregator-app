package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.PassengerResponse;

public interface PassengerService {
    PassengerResponse createPassenger(PassengerRequest passengerRequest);

    PassengerResponse editPassenger(long id, PassengerRequest passengerRequest);

    PassengerResponse getPassengerById(long id);
}
