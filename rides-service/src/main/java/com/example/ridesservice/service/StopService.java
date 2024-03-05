package com.example.ridesservice.service;

import com.example.ridesservice.dto.request.StopRequest;
import com.example.ridesservice.dto.response.StopsResponse;
import com.example.ridesservice.model.Ride;

import java.util.List;

public interface StopService {
    StopsResponse createStops(List<StopRequest> stopRequests, Ride ride);

    StopsResponse editStops(List<StopRequest> stops, Ride ride);

    StopsResponse getRideStops(Ride ride);
}