package com.example.ridesservice.exception;

public class PromoCodeAlreadyExistsException extends RuntimeException {
    public PromoCodeAlreadyExistsException(String message) {
        super(message);
    }
}
