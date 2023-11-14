package com.example.passengerservice.controller;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;
import com.example.passengerservice.service.PassengerRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passenger/rating")
@RequiredArgsConstructor
public class PassengerRatingController {
    private final PassengerRatingService passengerRatingService;

    @PostMapping()
    public PassengerRatingResponse ratePassenger(@RequestBody PassengerRatingRequest passengerRatingRequest) {
        return passengerRatingService.ratePassenger(passengerRatingRequest);
    }

    @GetMapping("/{id}")
    public List<PassengerRatingResponse> getRatingsByPassengerId(@PathVariable("id") long id) {
        return passengerRatingService.getRatingsByPassengerId(id);
    }

    @GetMapping("/average/{id}")
    public AveragePassengerRatingResponse getAveragePassengerRating(@PathVariable("id") long id) {
        return passengerRatingService.getAveragePassengerRating(id);
    }
}