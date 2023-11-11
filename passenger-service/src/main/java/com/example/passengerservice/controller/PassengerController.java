package com.example.passengerservice.controller;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passenger")
@RequiredArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;

    @PostMapping()
    public PassengerResponse createPassenger(@RequestBody PassengerRequest passengerRequest) {
        return passengerService.createPassenger(passengerRequest);
    }

    @PostMapping("/{id}")
    public PassengerResponse editPassenger(@RequestBody PassengerRequest passengerRequest,
                                           @PathVariable("id") long id) {
        return passengerService.editPassenger(id, passengerRequest);
    }
}