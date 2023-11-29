package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.ConfirmRideRequest;
import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.dto.response.RidesPageResponse;

public interface RideService {
    RideResponse createRide(CreateRideRequest createRideRequest);

    RideResponse editRide(Long rideId, EditRideRequest editRideRequest);

    RideResponse canselRide(Long rideId);

    RideResponse startRide(Long rideId);

    RideResponse completeRide(Long rideId);

    RideResponse getRideByRideId(Long rideId);

    RidesPageResponse getPassengerRides(Long passengerId, int page, int size, String sortBy);

    RidesPageResponse getDriverRides(Long driverId, int page, int size, String sortBy);
}