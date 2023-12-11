package com.example.driverservice.service;

import com.example.driverservice.dto.request.DriverRatingRequest;
import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;

public interface DriverRatingService {
    void rateDriver(DriverRatingRequest driverRatingRequest);

    AllDriverRatingsResponse getRatingsByDriverId(long driverId);

    AverageDriverRatingResponse getAverageDriverRating(long driverId);
}
