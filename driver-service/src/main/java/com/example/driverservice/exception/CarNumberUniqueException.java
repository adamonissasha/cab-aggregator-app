package com.example.driverservice.exception;

public class CarNumberUniqueException extends RuntimeException {
    public CarNumberUniqueException(String message) {
        super(message);
    }
}
