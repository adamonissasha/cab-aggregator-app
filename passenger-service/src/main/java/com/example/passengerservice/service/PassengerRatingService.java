package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;

import java.util.List;

public interface PassengerRatingService {
    PassengerRatingResponse ratePassenger(long passengerId, PassengerRatingRequest passengerRatingRequest);

    List<PassengerRatingResponse> getRatingsByPassengerId(long passengerId);

    AveragePassengerRatingResponse getAveragePassengerRating(long passengerId);
}
