package com.example.ridesservice.handler;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorResponse {
    private final List<String> errors = new ArrayList<>();

    public void addValidationError(String error) {
        errors.add(error);
    }
}