package com.example.ridesservice.exception;

public class IncorrectPaymentMethodException extends RuntimeException {
    public IncorrectPaymentMethodException(String message) {
        super(message);
    }
}
