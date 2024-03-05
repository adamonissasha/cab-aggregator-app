package com.example.passengerservice.controller;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.service.PassengerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/passenger")
@RequiredArgsConstructor
@Validated
public class PassengerController {
    private final PassengerService passengerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PassengerResponse> createPassenger(@Valid @RequestBody PassengerRequest passengerRequest) {
        return passengerService.createPassenger(passengerRequest);
    }

    @PutMapping("/{id}")
    public Mono<PassengerResponse> editPassenger(@Valid @RequestBody PassengerRequest passengerRequest,
                                                 @PathVariable("id") String id) {
        return passengerService.editPassenger(id, passengerRequest);
    }

    @GetMapping("/{id}")
    public Mono<PassengerResponse> getPassengerById(@PathVariable("id") String id) {
        return passengerService.getPassengerById(id);
    }

    @GetMapping
    public Mono<PassengerPageResponse> getAllPassengers(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                        @RequestParam(defaultValue = "10") @Min(1) int size,
                                                        @RequestParam(defaultValue = "id") String sortBy) {
        return passengerService.getAllPassengers(page, size, sortBy);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePassengerById(@PathVariable("id") String id) {
        return passengerService.deletePassengerById(id);
    }
}
