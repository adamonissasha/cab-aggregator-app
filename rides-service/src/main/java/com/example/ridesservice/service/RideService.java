package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.request.RatingRequest;
import com.example.ridesservice.dto.response.PassengerRideResponse;
import com.example.ridesservice.dto.response.PassengerRidesPageResponse;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.dto.response.RidesPageResponse;

public interface RideService {
    PassengerRideResponse createRide(CreateRideRequest createRideRequest);

    PassengerRideResponse editRide(Long rideId, EditRideRequest editRideRequest);

    RideResponse cancelRide(Long rideId);

    RideResponse startRide(Long rideId);

    RideResponse completeRide(Long rideId);

    RideResponse getRideByRideId(Long rideId);

    PassengerRidesPageResponse getPassengerRides(Long passengerId, int page, int size, String sortBy);

    RidesPageResponse getDriverRides(Long driverId, int page, int size, String sortBy);

    void ratePassenger(Long id, RatingRequest ratingRequest);

    void rateDriver(Long id, RatingRequest ratingRequest);
}