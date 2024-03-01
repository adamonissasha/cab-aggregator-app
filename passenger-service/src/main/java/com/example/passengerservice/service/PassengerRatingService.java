package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import reactor.core.publisher.Mono;

public interface PassengerRatingService {
    Mono<Void> ratePassenger(PassengerRatingRequest passengerRatingRequest);

    Mono<AllPassengerRatingsResponse> getRatingsByPassengerId(String passengerId);

    Mono<AveragePassengerRatingResponse> getAveragePassengerRating(String passengerId);
}
