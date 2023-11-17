package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.RideReservationRequest;
import com.example.ridesservice.dto.response.RideReservationResponse;

public interface RideReservationService {
    RideReservationResponse createRideReservation(RideReservationRequest rideReservationRequest, Long passengerId);

    RideReservationResponse editRideReservation(Long id, Long passengerId, RideReservationRequest rideReservationRequest);

    RideReservationResponse canselReservation(Long id, Long passengerId);
}
