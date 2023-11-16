package com.example.ridesservice.controller;

import com.example.ridesservice.dto.request.RideReservationRequest;
import com.example.ridesservice.dto.response.RideReservationResponse;
import com.example.ridesservice.service.RideReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ride/reservation/{passengerId}")
@RequiredArgsConstructor
public class RideReservationController {
    private final RideReservationService rideReservationService;

    @PostMapping()
    public RideReservationResponse createRideReservation(@PathVariable("passengerId") Long passengerId,
                                                         @RequestBody RideReservationRequest rideReservationRequest) {
        return rideReservationService.createRideReservation(rideReservationRequest, passengerId);
    }

    @PutMapping("/{id}")
    public RideReservationResponse editRideReservation(@PathVariable("passengerId") Long passengerId,
                                                       @PathVariable("id") Long id,
                                                       @RequestBody RideReservationRequest rideReservationRequest) {
        return rideReservationService.editRideReservation(id, passengerId, rideReservationRequest);
    }
}
