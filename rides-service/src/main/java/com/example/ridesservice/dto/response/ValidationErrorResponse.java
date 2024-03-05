package com.example.ridesservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidationErrorResponse {
    private List<String> errors;
    private int statusCode;
}