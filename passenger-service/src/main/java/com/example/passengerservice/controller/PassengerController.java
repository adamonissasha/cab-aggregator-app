package com.example.passengerservice.controller;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.service.PassengerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passenger")
@RequiredArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;

    @PostMapping
    public PassengerResponse createPassenger(@Valid @RequestBody PassengerRequest passengerRequest) {
        return passengerService.createPassenger(passengerRequest);
    }

    @PutMapping("/{id}")
    public PassengerResponse editPassenger(@Valid @RequestBody PassengerRequest passengerRequest,
                                           @PathVariable("id") long id) {
        return passengerService.editPassenger(id, passengerRequest);
    }

    @GetMapping("/{id}")
    public PassengerResponse getPassengerById(@PathVariable("id") long id) {
        return passengerService.getPassengerById(id);
    }

    @GetMapping
    public Page<PassengerResponse> getAllPassengers(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                    @RequestParam(defaultValue = "10") @Min(1) int size,
                                                    @RequestParam(defaultValue = "id") String sortBy) {
        return passengerService.getAllPassengers(page, size, sortBy);
    }
}
