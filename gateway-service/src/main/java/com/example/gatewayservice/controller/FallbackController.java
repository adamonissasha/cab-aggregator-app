package com.example.gatewayservice.controller;

import com.example.gatewayservice.response.FallbackResponse;
import com.example.gatewayservice.service.FallbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FallbackController {
    private final FallbackService fallbackService;

    @GetMapping("/fallback/passenger")
    public FallbackResponse fallbackForPassenger() {
        return fallbackService.getFallbackResponse("Fallback response for passenger-service");
    }

    @GetMapping("/fallback/driver")
    public FallbackResponse fallbackForDriver() {
        return fallbackService.getFallbackResponse("Fallback response for driver-service");
    }

    @GetMapping("/fallback/ride")
    public FallbackResponse fallbackForRide() {
        return fallbackService.getFallbackResponse("Fallback response for ride-service");
    }

    @GetMapping("/fallback/bank")
    public FallbackResponse fallbackForBank() {
        return fallbackService.getFallbackResponse("Fallback response for bank-service");
    }
}
