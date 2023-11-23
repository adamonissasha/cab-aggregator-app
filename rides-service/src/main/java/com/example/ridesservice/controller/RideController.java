package com.example.ridesservice.controller;

import com.example.ridesservice.dto.request.ConfirmRideRequest;
import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.dto.response.RidesPageResponse;
import com.example.ridesservice.service.RideService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ride")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RideResponse createRide(@Valid @RequestBody CreateRideRequest createRideRequest) {
        return rideService.createRide(createRideRequest);
    }

    @PutMapping("/{rideId}")
    @ResponseStatus(HttpStatus.OK)
    public RideResponse editRide(@PathVariable("rideId") Long rideId,
                                 @Valid @RequestBody EditRideRequest editRideRequest) {
        return rideService.editRide(rideId, editRideRequest);
    }

    @PutMapping("/cancel/{rideId}")
    @ResponseStatus(HttpStatus.OK)
    public RideResponse canselRide(@PathVariable("rideId") Long rideId) {
        return rideService.canselRide(rideId);
    }

    @PutMapping("/confirm/{rideId}")
    @ResponseStatus(HttpStatus.OK)
    public RideResponse confirmRide(@PathVariable("rideId") Long rideId,
                                    @Valid @RequestBody ConfirmRideRequest confirmRideRequest) {
        return rideService.confirmRide(rideId, confirmRideRequest);
    }

    @PutMapping("/start/{rideId}")
    @ResponseStatus(HttpStatus.OK)
    public RideResponse startRide(@PathVariable("rideId") Long rideId) {
        return rideService.startRide(rideId);
    }

    @PutMapping("/complete/{rideId}")
    @ResponseStatus(HttpStatus.OK)
    public RideResponse completeRide(@PathVariable("rideId") Long rideId) {
        return rideService.completeRide(rideId);
    }

    @GetMapping("/{rideId}")
    @ResponseStatus(HttpStatus.OK)
    public RideResponse getRideByRideId(@PathVariable("rideId") Long rideId) {
        return rideService.getRideByRideId(rideId);
    }

    @GetMapping("/available")
    @ResponseStatus(HttpStatus.OK)
    public RidesPageResponse getAvailableRides(@RequestParam(defaultValue = "0") @Min(0) int page,
                                               @RequestParam(defaultValue = "10") @Min(1) int size,
                                               @RequestParam(defaultValue = "id") String sortBy) {
        return rideService.getAvailableRides(page, size, sortBy);
    }

    @GetMapping("/passenger/{passengerId}")
    @ResponseStatus(HttpStatus.OK)
    public RidesPageResponse getPassengerRides(@PathVariable("passengerId") Long passengerId,
                                               @RequestParam(defaultValue = "0") @Min(0) int page,
                                               @RequestParam(defaultValue = "10") @Min(1) int size,
                                               @RequestParam(defaultValue = "id") String sortBy) {
        return rideService.getPassengerRides(passengerId, page, size, sortBy);
    }

    @GetMapping("/driver/{driverId}")
    @ResponseStatus(HttpStatus.OK)
    public RidesPageResponse getDriverRides(@PathVariable("driverId") Long driverId,
                                            @RequestParam(defaultValue = "0") @Min(0) int page,
                                            @RequestParam(defaultValue = "10") @Min(1) int size,
                                            @RequestParam(defaultValue = "id") String sortBy) {
        return rideService.getDriverRides(driverId, page, size, sortBy);
    }
}
