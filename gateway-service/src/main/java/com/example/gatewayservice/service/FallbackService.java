package com.example.gatewayservice.service;

import com.example.gatewayservice.response.FallbackResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FallbackService {
    public FallbackResponse getFallbackResponse(String message) {
        return FallbackResponse.builder()
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message(message)
                .build();
    }
}
