package com.example.ridesservice.exception;

public class PaymentMethodException extends RuntimeException {
    public PaymentMethodException(String message) {
        super(message);
    }
}
