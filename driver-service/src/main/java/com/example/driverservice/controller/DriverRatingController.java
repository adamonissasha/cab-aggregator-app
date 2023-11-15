package com.example.driverservice.controller;

import com.example.driverservice.dto.request.DriverRatingRequest;
import com.example.driverservice.dto.response.AllDriversResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.DriverRatingResponse;
import com.example.driverservice.service.DriverRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver/{driverId}/rating")
@RequiredArgsConstructor
public class DriverRatingController {
    private final DriverRatingService driverRatingService;

    @PostMapping()
    public DriverRatingResponse rateDriver(@Valid @RequestBody DriverRatingRequest driverRatingRequest,
                                           @PathVariable("driverId") long driverId) {
        return driverRatingService.rateDriver(driverRatingRequest, driverId);
    }

    @GetMapping()
    public AllDriversResponse getRatingsByDriverId(@PathVariable("driverId") long driverId) {
        return driverRatingService.getRatingsByDriverId(driverId);
    }

    @GetMapping("/average")
    public AverageDriverRatingResponse getAverageDriverRating(@PathVariable("driverId") long driverId) {
        return driverRatingService.getAverageDriverRating(driverId);
    }
}