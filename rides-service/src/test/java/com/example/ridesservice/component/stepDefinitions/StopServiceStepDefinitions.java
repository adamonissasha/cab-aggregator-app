package com.example.ridesservice.component.stepDefinitions;


import com.example.ridesservice.dto.request.StopRequest;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.dto.response.StopsResponse;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.Stop;
import com.example.ridesservice.repository.StopRepository;
import com.example.ridesservice.service.impl.StopServiceImpl;
import com.example.ridesservice.util.TestRideUtil;
import com.example.ridesservice.util.TestStopUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class StopServiceStepDefinitions {
    @Mock
    StopRepository stopRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    StopServiceImpl stopService;

    private Ride ride;
    private StopsResponse expected;
    private StopsResponse actual;
    private List<StopRequest> stopRequests;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("Provided valid ride requests and ride")
    public void providedValidRideRequestsAndRide() {
        ride = TestRideUtil.getFirstRide();

        Stop firstStop = TestStopUtil.getFirstStop();
        Stop secondStop = TestStopUtil.getSecondStop();

        StopResponse firstStopResponse = TestStopUtil.getStopResponse();
        StopResponse secondStopResponse = TestStopUtil.getSecondStopResponse();

        stopRequests = Arrays.asList(TestStopUtil.getFirstStopRequest(), TestStopUtil.getSecondStopRequest());
        List<StopResponse> stopResponses = Arrays.asList(firstStopResponse, secondStopResponse);

        expected = StopsResponse.builder()
                .stops(stopResponses)
                .build();

        when(stopRepository.save(any(Stop.class)))
                .thenReturn(firstStop, secondStop);
        when(modelMapper.map(any(Stop.class), eq(StopResponse.class)))
                .thenReturn(firstStopResponse, secondStopResponse);

        assertFalse(stopRequests.isEmpty());
    }

    @Given("Provided empty stop requests")
    public void providedEmptyStopRequests() {
        ride = TestRideUtil.getFirstRide();
        stopRequests = Collections.emptyList();
    }

    @When("Method createStops called")
    public void methodCreateStopsCalled() {
        actual = stopService.createStops(stopRequests, ride);
    }

    @Then("The response should contain the details of ride stops")
    public void theResponseShouldContainTheDetailsOfRideStops() {
        assertEquals(actual, expected);
    }

    @Then("The response should contain empty list of stops")
    public void theResponseShouldContainEmptyListOfStops() {
        assertTrue(actual.getStops().isEmpty());
    }

    @Given("Provided valid ride and ride has stops")
    public void providedValidRideAndRideHasStops() {
        ride = TestRideUtil.getFirstRide();

        Stop firstStop = TestStopUtil.getFirstStop();
        Stop secondStop = TestStopUtil.getSecondStop();
        List<Stop> stops = Arrays.asList(firstStop, secondStop);

        List<StopResponse> stopResponses = Arrays.asList(TestStopUtil.getStopResponse(), TestStopUtil.getSecondStopResponse());
        expected = StopsResponse.builder()
                .stops(stopResponses)
                .build();

        when(stopRepository.findByRide(ride))
                .thenReturn(stops);
        when(modelMapper.map(firstStop, StopResponse.class))
                .thenReturn(stopResponses.get(0));
        when(modelMapper.map(secondStop, StopResponse.class))
                .thenReturn(stopResponses.get(1));

        assertFalse(stopRepository.findByRide(ride).isEmpty());
    }

    @Given("Provided valid ride and ride hasn't stops")
    public void providedValidRideAndRideHasNotStops() {
        ride = TestRideUtil.getFirstRide();

        when(stopRepository.findByRide(ride))
                .thenReturn(Collections.emptyList());

        assertTrue(stopRepository.findByRide(ride).isEmpty());
    }

    @When("Method getRideStops called")
    public void methodGetRideStopsCalled() {
        actual = stopService.getRideStops(ride);
    }

    @Given("Provided valid ride that has stops")
    public void providedValidRideThatHasStops() {
        ride = TestRideUtil.getFirstRide();
        stopRequests = Arrays.asList(TestStopUtil.getFirstStopRequest(), TestStopUtil.getSecondStopRequest());

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

        expected = StopsResponse.builder()
                .stops(List.of(firstStopResponse, secondStopResponse))
                .build();

        when(stopRepository.findByRide(ride))
                .thenReturn(stops);
        doNothing()
                .when(stopRepository)
                .deleteAll(stops);
        when(stopRepository.saveAll(anyList()))
                .thenReturn(newStops);
        when(modelMapper.map(any(Stop.class), eq(StopResponse.class)))
                .thenReturn(firstStopResponse, secondStopResponse);

        assertFalse(stopRepository.findByRide(ride).isEmpty());
    }

    @Given("Provided valid ride that hasn't stops")
    public void providedValidRideThatHasNotStops() {
        ride = TestRideUtil.getFirstRide();
        stopRequests = Arrays.asList(TestStopUtil.getFirstStopRequest(), TestStopUtil.getSecondStopRequest());

        StopResponse firstStopResponse = TestStopUtil.getStopResponse();
        StopResponse secondStopResponse = TestStopUtil.getSecondStopResponse();
        expected = StopsResponse.builder()
                .stops(List.of(firstStopResponse, secondStopResponse))
                .build();

        when(stopRepository.findByRide(ride))
                .thenReturn(Collections.emptyList());

        List<Stop> savedNewStops = stopRequests.stream()
                .map(stopRequest -> Stop.builder()
                        .number(stopRequest.getNumber())
                        .address(stopRequest.getAddress())
                        .ride(ride)
                        .build())
                .toList();

        when(stopRepository.saveAll(anyList()))
                .thenReturn(savedNewStops);
        when(modelMapper.map(any(Stop.class), eq(StopResponse.class)))
                .thenReturn(firstStopResponse, secondStopResponse);

        assertTrue(stopRepository.findByRide(ride).isEmpty());
    }

    @When("Method editStops called")
    public void methodEditStopsCalled() {
        actual = stopService.editStops(stopRequests, ride);
    }
}
