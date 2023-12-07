package com.example.driverservice.controller;

import com.example.driverservice.dto.request.DriverRatingRequest;
import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.DriverRatingResponse;
import com.example.driverservice.service.DriverRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/driver/{driverId}/rating")
@RequiredArgsConstructor
public class DriverRatingController {
    private final DriverRatingService driverRatingService;

    @PostMapping
    public DriverRatingResponse rateDriver(@Valid @RequestBody DriverRatingRequest driverRatingRequest,
                                           @PathVariable("driverId") long driverId) {
        return driverRatingService.rateDriver(driverRatingRequest, driverId);
    }

    @GetMapping
    public AllDriverRatingsResponse getRatingsByDriverId(@PathVariable("driverId") long driverId) {
        return driverRatingService.getRatingsByDriverId(driverId);
    }

    @GetMapping("/average")
    public AverageDriverRatingResponse getAverageDriverRating(@PathVariable("driverId") long driverId) {
        return driverRatingService.getAverageDriverRating(driverId);
    }
}