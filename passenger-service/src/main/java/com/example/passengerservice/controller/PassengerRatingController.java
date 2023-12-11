package com.example.passengerservice.controller;

import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.service.PassengerRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passenger/{passengerId}/rating")
@RequiredArgsConstructor
public class PassengerRatingController {
    private final PassengerRatingService passengerRatingService;

    @GetMapping
    public AllPassengerRatingsResponse getRatingsByPassengerId(@PathVariable("passengerId") long passengerId) {
        return passengerRatingService.getRatingsByPassengerId(passengerId);
    }

    @GetMapping("/average")
    public AveragePassengerRatingResponse getAveragePassengerRating(@PathVariable("passengerId") long passengerId) {
        return passengerRatingService.getAveragePassengerRating(passengerId);
    }
}