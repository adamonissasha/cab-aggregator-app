package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.RidePassengerResponse;

public interface PassengerService {
    PassengerResponse createPassenger(PassengerRequest passengerRequest);

    PassengerResponse editPassenger(long id, PassengerRequest passengerRequest);

    RidePassengerResponse getPassengerById(long id);

    PassengerPageResponse getAllPassengers(int page, int size, String sortBy);
}
