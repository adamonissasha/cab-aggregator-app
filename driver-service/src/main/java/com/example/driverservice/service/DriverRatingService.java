package com.example.driverservice.service;

import com.example.driverservice.dto.request.DriverRatingRequest;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.DriverRatingResponse;

import java.util.List;

public interface DriverRatingService {
    DriverRatingResponse rateDriver(DriverRatingRequest driverRatingRequest, long driverId);

    List<DriverRatingResponse> getRatingsByDriverId(long driverId);

    AverageDriverRatingResponse getAverageDriverRating(long driverId);
}
