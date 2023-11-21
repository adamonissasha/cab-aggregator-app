package com.example.ridesservice.service.impl;

import com.example.ridesservice.dto.request.StopRequest;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.Stop;
import com.example.ridesservice.repository.StopRepository;
import com.example.ridesservice.service.StopService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StopServiceImpl implements StopService {
    private final StopRepository stopRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<StopResponse> createStops(List<StopRequest> stopRequests, Ride ride) {
        return stopRequests.stream()
                .map(stopRequest -> Stop.builder()
                        .number(stopRequests.indexOf(stopRequest) + 1)
                        .address(stopRequest.getAddress())
                        .ride(ride)
                        .build())
                .map(stopRepository::save)
                .map(this::mapStopToStopResponse)
                .toList();
    }

    @Override
    public List<StopResponse> editStops(List<StopRequest> stops, Ride ride) {
        List<Stop> existingStops = stopRepository.findByRide(ride);
        stopRepository.deleteAll(existingStops);
        List<Stop> newStops = stops.stream()
                .map(stopRequest -> Stop.builder()
                        .number(stops.indexOf(stopRequest) + 1)
                        .address(stopRequest.getAddress())
                        .ride(ride)
                        .build())
                .toList();
        List<Stop> savedStops = stopRepository.saveAll(newStops);
        return savedStops.stream()
                .map(this::mapStopToStopResponse)
                .toList();
    }

    @Override
    public List<StopResponse> getRideStops(Ride ride) {
        return stopRepository.findByRide(ride)
                .stream()
                .map(this::mapStopToStopResponse)
                .toList();
    }

    public StopResponse mapStopToStopResponse(Stop stop) {
        return modelMapper.map(stop, StopResponse.class);
    }
}
