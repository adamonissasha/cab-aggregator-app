package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.response.RideResponse;
import org.springframework.data.domain.Page;

public interface RideService {
    RideResponse createRide(CreateRideRequest createRideRequest);

    RideResponse editRide(Long rideId, EditRideRequest editRideRequest);

    RideResponse canselRide(Long rideId);

    RideResponse confirmRide(Long rideId, Long driverId);

    RideResponse startRide(Long rideId);

    RideResponse completeRide(Long rideId);

    RideResponse getRideByRideId(Long rideId);

    Page<RideResponse> getAvailableRides(int page, int size, String sortBy);

    Page<RideResponse> getPassengerRides(Long passengerId, int page, int size, String sortBy);

    Page<RideResponse> getDriverRides(Long driverId, int page, int size, String sortBy);
}