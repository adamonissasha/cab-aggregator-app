package com.example.gatewayservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FallbackResponse {
    private int statusCode;
    private HttpStatus httpStatus;
    protected String message;
}
