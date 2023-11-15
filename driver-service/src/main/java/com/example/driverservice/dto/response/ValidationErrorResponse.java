package com.example.driverservice.dto.response;

import java.util.List;

public record ValidationErrorResponse(List<String> errors) {
    public void addValidationError(String error) {
        errors.add(error);
    }
}