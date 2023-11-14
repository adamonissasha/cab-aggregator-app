package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;

import java.util.List;

public interface PassengerRatingService {
    PassengerRatingResponse ratePassenger(PassengerRatingRequest passengerRatingRequest);

    List<PassengerRatingResponse> getRatingsByPassengerId(long id);

    AveragePassengerRatingResponse getAveragePassengerRating(long id);
}
