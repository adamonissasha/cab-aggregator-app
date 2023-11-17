package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.StopRequest;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.model.RideReservation;

import java.util.List;

public interface StopService {
    List<StopResponse> createStops(List<StopRequest> stopRequests, RideReservation rideReservation);

    List<StopResponse> editStops(List<StopRequest> stops, RideReservation rideReservation);

    List<StopResponse> getRideReservationStops(RideReservation rideReservation);
}
