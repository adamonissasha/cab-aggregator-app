package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.response.RideResponse;

public interface RideService {
    RideResponse createRide(CreateRideRequest createRideRequest);

    RideResponse editRide(Long rideId, EditRideRequest editRideRequest);

    RideResponse canselRide(Long rideId);

    RideResponse confirmRide(Long rideId, Long driverId);

    RideResponse startRide(Long rideId);

    RideResponse completeRide(Long rideId);

    RideResponse getRideByRideId(Long rideId);
}