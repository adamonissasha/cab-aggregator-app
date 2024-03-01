package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import reactor.core.publisher.Mono;

public interface PassengerService {
    Mono<PassengerResponse> createPassenger(PassengerRequest passengerRequest);

    Mono<PassengerResponse> editPassenger(String id, PassengerRequest passengerRequest);

    Mono<PassengerResponse> getPassengerById(String id);

    Mono<PassengerPageResponse> getAllPassengers(int page, int size, String sortBy);

    Mono<Void> deletePassengerById(String id);
}
