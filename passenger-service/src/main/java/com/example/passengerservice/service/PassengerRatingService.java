package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;

public interface PassengerRatingService {
    PassengerRatingResponse ratePassenger(long passengerId, PassengerRatingRequest passengerRatingRequest);

    AllPassengerRatingsResponse getRatingsByPassengerId(long passengerId);

    AveragePassengerRatingResponse getAveragePassengerRating(long passengerId);
}
