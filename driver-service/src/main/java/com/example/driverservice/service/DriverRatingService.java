package com.example.driverservice.service;

import com.example.driverservice.dto.request.DriverRatingRequest;
import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.DriverRatingResponse;

public interface DriverRatingService {
    DriverRatingResponse rateDriver(DriverRatingRequest driverRatingRequest, long driverId);

    AllDriverRatingsResponse getRatingsByDriverId(long driverId);

    AverageDriverRatingResponse getAverageDriverRating(long driverId);
}
