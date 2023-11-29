package com.example.driverservice.exception;

public class FreeDriverNotFoundException extends RuntimeException {
    public FreeDriverNotFoundException(String message) {
        super(message);
    }
}
