package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;

public interface PassengerRatingService {
    void ratePassenger(PassengerRatingRequest passengerRatingRequest);

    AllPassengerRatingsResponse getRatingsByPassengerId(long passengerId);

    AveragePassengerRatingResponse getAveragePassengerRating(long passengerId);
}
