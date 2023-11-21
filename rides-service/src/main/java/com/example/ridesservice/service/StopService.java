package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.StopRequest;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.model.Ride;

import java.util.List;

public interface StopService {
    List<StopResponse> createStops(List<StopRequest> stopRequests, Ride ride);

    List<StopResponse> editStops(List<StopRequest> stops, Ride ride);

    List<StopResponse> getRideStops(Ride ride);
}