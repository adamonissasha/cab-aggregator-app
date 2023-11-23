package com.example.passengerservice.controller;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;
import com.example.passengerservice.service.PassengerRatingService;
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
@RequestMapping("/passenger/{passengerId}/rating")
@RequiredArgsConstructor
public class PassengerRatingController {
    private final PassengerRatingService passengerRatingService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public PassengerRatingResponse ratePassenger(@PathVariable("passengerId") long passengerId,
                                                 @Valid @RequestBody PassengerRatingRequest passengerRatingRequest) {
        return passengerRatingService.ratePassenger(passengerId, passengerRatingRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public AllPassengerRatingsResponse getRatingsByPassengerId(@PathVariable("passengerId") long passengerId) {
        return passengerRatingService.getRatingsByPassengerId(passengerId);
    }

    @GetMapping("/average")
    @ResponseStatus(HttpStatus.OK)
    public AveragePassengerRatingResponse getAveragePassengerRating(@PathVariable("passengerId") long passengerId) {
        return passengerRatingService.getAveragePassengerRating(passengerId);
    }
}