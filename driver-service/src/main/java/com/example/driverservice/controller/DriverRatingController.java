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
    @ResponseStatus(HttpStatus.OK)
    public DriverRatingResponse rateDriver(@Valid @RequestBody DriverRatingRequest driverRatingRequest,
                                           @PathVariable("driverId") long driverId) {
        return driverRatingService.rateDriver(driverRatingRequest, driverId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public AllDriverRatingsResponse getRatingsByDriverId(@PathVariable("driverId") long driverId) {
        return driverRatingService.getRatingsByDriverId(driverId);
    }

    @GetMapping("/average")
    @ResponseStatus(HttpStatus.OK)
    public AverageDriverRatingResponse getAverageDriverRating(@PathVariable("driverId") long driverId) {
        return driverRatingService.getAverageDriverRating(driverId);
    }
}