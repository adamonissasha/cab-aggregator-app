package com.example.ridesservice.exception.driver;

public class FreeDriverNotFoundException extends RuntimeException {
    public FreeDriverNotFoundException(String message) {
        super(message);
    }
}