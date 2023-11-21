package com.example.ridesservice.controller;

import com.example.ridesservice.dto.request.ConfirmRideRequest;
import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.service.RideService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ride")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @PostMapping()
    public RideResponse createRide(@Valid @RequestBody CreateRideRequest createRideRequest) {
        return rideService.createRide(createRideRequest);
    }

    @PutMapping("/{rideId}")
    public RideResponse editRide(@PathVariable("rideId") Long rideId,
                                 @Valid @RequestBody EditRideRequest editRideRequest) {
        return rideService.editRide(rideId, editRideRequest);
    }

    @PutMapping("/cancel/{rideId}")
    public RideResponse canselRide(@PathVariable("rideId") Long rideId) {
        return rideService.canselRide(rideId);
    }

    @PutMapping("/confirm/{rideId}")
    public RideResponse confirmRide(@PathVariable("rideId") Long rideId,
                                    @Valid @RequestBody ConfirmRideRequest confirmRideRequest) {
        return rideService.confirmRide(rideId, confirmRideRequest);
    }

    @PutMapping("/start/{rideId}")
    public RideResponse startRide(@PathVariable("rideId") Long rideId) {
        return rideService.startRide(rideId);
    }

    @PutMapping("/complete/{rideId}")
    public RideResponse completeRide(@PathVariable("rideId") Long rideId) {
        return rideService.completeRide(rideId);
    }

    @GetMapping("/{rideId}")
    public RideResponse getRideByRideId(@PathVariable("rideId") Long rideId) {
        return rideService.getRideByRideId(rideId);
    }


    @GetMapping("/available")
    public Page<RideResponse> getAvailableRides(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                @RequestParam(defaultValue = "10") @Min(1) int size,
                                                @RequestParam(defaultValue = "id") String sortBy) {
        return rideService.getAvailableRides(page, size, sortBy);
    }

    @GetMapping("/passenger/{passengerId}")
    public Page<RideResponse> getPassengerRides(@PathVariable("passengerId") Long passengerId,
                                                @RequestParam(defaultValue = "0") @Min(0) int page,
                                                @RequestParam(defaultValue = "10") @Min(1) int size,
                                                @RequestParam(defaultValue = "id") String sortBy) {
        return rideService.getPassengerRides(passengerId, page, size, sortBy);
    }

    @GetMapping("/driver/{driverId}")
    public Page<RideResponse> getDriverRides(@PathVariable("driverId") Long driverId,
                                             @RequestParam(defaultValue = "0") @Min(0) int page,
                                             @RequestParam(defaultValue = "10") @Min(1) int size,
                                             @RequestParam(defaultValue = "id") String sortBy) {
        return rideService.getDriverRides(driverId, page, size, sortBy);
    }
}
