package com.example.driverservice.exception;

public class DriverRatingNotFoundException extends RuntimeException {
    public DriverRatingNotFoundException(String message) {
        super(message);
    }
}
