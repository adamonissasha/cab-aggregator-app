package com.example.ridesservice.service;


import com.example.ridesservice.dto.request.StopRequest;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.Stop;
import com.example.ridesservice.repository.StopRepository;
import com.example.ridesservice.service.impl.StopServiceImpl;
import com.example.ridesservice.util.TestRideUtil;
import com.example.ridesservice.util.TestStopUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StopServiceTest {
    @Mock
    StopRepository stopRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    StopServiceImpl stopService;

    @Test
    void testCreateStops_WhenStopRequestsProvided_ShouldCreateStopsForRide() {
        Ride ride = TestRideUtil.getFirstRide();
        StopRequest firstStopRequest = TestStopUtil.getFirstStopRequest();
        StopRequest secondStopRequest = TestStopUtil.getSecondStopRequest();
        Stop firstStop = TestStopUtil.getFirstStop();
        Stop secondStop = TestStopUtil.getSecondStop();
        StopResponse firstStopResponse = TestStopUtil.getStopResponse();
        StopResponse secondStopResponse = TestStopUtil.getSecondStopResponse();

        List<StopRequest> stopRequests = Arrays.asList(firstStopRequest, secondStopRequest);
        List<StopResponse> stopResponses = Arrays.asList(firstStopResponse, secondStopResponse);

        when(stopRepository.save(any(Stop.class))).thenReturn(firstStop, secondStop);
        when(modelMapper.map(any(Stop.class), eq(StopResponse.class))).thenReturn(firstStopResponse, secondStopResponse);

        List<StopResponse> createdStops = stopService.createStops(stopRequests, ride);

        assertEquals(stopResponses.size(), createdStops.size());
        for (int i = 0; i < stopResponses.size(); i++) {
            assertEquals(stopResponses.get(i).getNumber(), createdStops.get(i).getNumber());
            assertEquals(stopResponses.get(i).getAddress(), createdStops.get(i).getAddress());
        }
        verify(stopRepository, times(stopRequests.size())).save(any(Stop.class));
    }

    @Test
    void testCreateStops_WhenEmptyStopRequestsProvided_ShouldReturnEmptyList() {
        Ride ride = TestRideUtil.getFirstRide();
        List<StopRequest> stopRequests = Collections.emptyList();
        List<StopResponse> createdStops = stopService.createStops(stopRequests, ride);

        assertTrue(createdStops.isEmpty());
    }

    @Test
    void testEditStops_WhenStopsExistForRide_ShouldEditAndReturnStopResponses() {
        Ride ride = TestRideUtil.getFirstRide();
        StopRequest firstStopRequest = TestStopUtil.getFirstStopRequest();
        StopRequest secondStopRequest = TestStopUtil.getSecondStopRequest();
        List<StopRequest> stopRequests = Arrays.asList(firstStopRequest, secondStopRequest);

        Stop firstStop = TestStopUtil.getFirstStop();
        Stop secondStop = TestStopUtil.getSecondStop();
        List<Stop> stops = Arrays.asList(firstStop, secondStop);

        StopResponse firstStopResponse = TestStopUtil.getStopResponse();
        StopResponse secondStopResponse = TestStopUtil.getSecondStopResponse();

        List<Stop> newStops = stopRequests.stream()
                .map(stopRequest -> Stop.builder()
                        .number(stopRequest.getNumber())
                        .address(stopRequest.getAddress())
                        .ride(ride)
                        .build())
                .toList();

        when(stopRepository.findByRide(ride)).thenReturn(stops);
        doNothing().when(stopRepository).deleteAll(stops);
        when(stopRepository.saveAll(anyList())).thenReturn(newStops);
        when(modelMapper.map(any(Stop.class), eq(StopResponse.class))).thenReturn(firstStopResponse, secondStopResponse);

        List<StopResponse> actualStopResponses = stopService.editStops(stopRequests, ride);

        assertEquals(stops.size(), actualStopResponses.size());
    }

    @Test
    void testEditStops_WhenNoStopsForRide_ShouldCreateAndReturnStopResponses() {
        Ride ride = TestRideUtil.getFirstRide();
        StopRequest firstStopRequest = TestStopUtil.getFirstStopRequest();
        StopRequest secondStopRequest = TestStopUtil.getSecondStopRequest();
        List<StopRequest> stopRequests = Arrays.asList(firstStopRequest, secondStopRequest);

        StopResponse firstStopResponse = TestStopUtil.getStopResponse();
        StopResponse secondStopResponse = TestStopUtil.getSecondStopResponse();

        when(stopRepository.findByRide(ride)).thenReturn(Collections.emptyList());

        List<Stop> savedNewStops = stopRequests.stream()
                .map(stopRequest -> Stop.builder()
                        .number(stopRequest.getNumber())
                        .address(stopRequest.getAddress())
                        .ride(ride)
                        .build())
                .toList();

        when(stopRepository.saveAll(anyList())).thenReturn(savedNewStops);
        when(modelMapper.map(any(Stop.class), eq(StopResponse.class))).thenReturn(firstStopResponse, secondStopResponse);

        List<StopResponse> actualStopResponses = stopService.editStops(stopRequests, ride);

        assertEquals(stopRequests.size(), actualStopResponses.size());
    }

    @Test
    void testGetRideStops_WhenValidRideProvided_ShouldReturnStopResponses() {
        Ride ride = TestRideUtil.getFirstRide();
        Stop firstStop = TestStopUtil.getFirstStop();
        Stop secondStop = TestStopUtil.getSecondStop();
        List<Stop> stops = Arrays.asList(firstStop, secondStop);

        StopResponse firstStopResponse = TestStopUtil.getStopResponse();
        StopResponse secondStopResponse = TestStopUtil.getSecondStopResponse();
        List<StopResponse> stopResponses = Arrays.asList(firstStopResponse, secondStopResponse);

        when(stopRepository.findByRide(ride)).thenReturn(stops);

        when(modelMapper.map(firstStop, StopResponse.class)).thenReturn(stopResponses.get(0));
        when(modelMapper.map(secondStop, StopResponse.class)).thenReturn(stopResponses.get(1));

        List<StopResponse> actualStopResponses = stopService.getRideStops(ride);

        assertEquals(stopResponses.size(), actualStopResponses.size());
    }

    @Test
    void testGetRideStops_WhenNoStopsForRide_ShouldReturnEmptyList() {
        Ride ride = TestRideUtil.getFirstRide();

        when(stopRepository.findByRide(ride)).thenReturn(Collections.emptyList());

        List<StopResponse> actualStopResponses = stopService.getRideStops(ride);

        assertTrue(actualStopResponses.isEmpty());
    }
}