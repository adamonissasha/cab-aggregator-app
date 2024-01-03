package com.example.ridesservice.controller;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.request.RatingRequest;
import com.example.ridesservice.dto.response.PassengerRideResponse;
import com.example.ridesservice.dto.response.PassengerRidesPageResponse;
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
    public PassengerRideResponse createRide(@Valid @RequestBody CreateRideRequest createRideRequest) {
        return rideService.createRide(createRideRequest);
    }

    @PutMapping("/{rideId}")
    public PassengerRideResponse editRide(@PathVariable("rideId") Long rideId,
                                          @Valid @RequestBody EditRideRequest editRideRequest) {
        return rideService.editRide(rideId, editRideRequest);
    }

    @PutMapping("/{rideId}/cancel")
    public RideResponse canselRide(@PathVariable("rideId") Long rideId) {
        return rideService.cancelRide(rideId);
    }

    @PutMapping("/{rideId}/start")
    public RideResponse startRide(@PathVariable("rideId") Long rideId) {
        return rideService.startRide(rideId);
    }

    @PutMapping("/{rideId}/complete")
    public RideResponse completeRide(@PathVariable("rideId") Long rideId) {
        return rideService.completeRide(rideId);
    }

    @GetMapping("/{rideId}")
    public RideResponse getRideByRideId(@PathVariable("rideId") Long rideId) {
        return rideService.getRideByRideId(rideId);
    }

    @GetMapping("/passenger/{passengerId}")
    public PassengerRidesPageResponse getPassengerRides(@PathVariable("passengerId") Long passengerId,
                                                        @RequestParam(defaultValue = "0") @Min(0) int page,
                                                        @RequestParam(defaultValue = "10") @Min(1) int size,
                                                        @RequestParam(defaultValue = "id") String sortBy) {
        return rideService.getPassengerRides(passengerId, page, size, sortBy);
    }

    @GetMapping("/driver/{driverId}")
    public RidesPageResponse getDriverRides(@PathVariable("driverId") Long driverId,
                                            @RequestParam(defaultValue = "0") @Min(0) int page,
                                            @RequestParam(defaultValue = "10") @Min(1) int size,
                                            @RequestParam(defaultValue = "id") String sortBy) {
        return rideService.getDriverRides(driverId, page, size, sortBy);
    }

    @PostMapping("/{id}/passenger/rate")
    public void ratePassenger(@PathVariable("id") Long id,
                              @Valid @RequestBody RatingRequest ratingRequest) {
        rideService.ratePassenger(id, ratingRequest);
    }

    @PostMapping("/{id}/driver/rate")
    public void rateDriver(@PathVariable("id") Long id,
                           @Valid @RequestBody RatingRequest ratingRequest) {
        rideService.rateDriver(id, ratingRequest);
    }
}