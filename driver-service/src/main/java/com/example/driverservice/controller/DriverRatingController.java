package com.example.driverservice.controller;

import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.service.DriverRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/driver/{driverId}/rating")
@RequiredArgsConstructor
public class DriverRatingController {
    private final DriverRatingService driverRatingService;

    @GetMapping
    public AllDriverRatingsResponse getRatingsByDriverId(@PathVariable("driverId") long driverId) {
        return driverRatingService.getRatingsByDriverId(driverId);
    }

    @GetMapping("/average")
    public AverageDriverRatingResponse getAverageDriverRating(@PathVariable("driverId") long driverId) {
        return driverRatingService.getAverageDriverRating(driverId);
    }
}