package com.example.ridesservice.controller;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ride")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @PostMapping()
    public RideResponse createRide(@RequestBody CreateRideRequest createRideRequest) {
        return rideService.createRide(createRideRequest);
    }

    @PutMapping("/{rideId}")
    public RideResponse editRide(@PathVariable("rideId") Long rideId,
                                 @RequestBody EditRideRequest editRideRequest) {
        return rideService.editRide(rideId, editRideRequest);
    }

    @PutMapping("/cancel/{rideId}")
    public RideResponse canselRide(@PathVariable("rideId") Long rideId) {
        return rideService.canselRide(rideId);
    }

    @PutMapping("/confirm/{rideId}/{driverId}")
    public RideResponse confirmRide(@PathVariable("rideId") Long rideId,
                                    @PathVariable("driverId") Long driverId) {
        return rideService.confirmRide(rideId, driverId);
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
}
