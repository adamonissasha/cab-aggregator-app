package com.example.bankservice.exception;

public class CardNumberUniqueException extends RuntimeException {
    public CardNumberUniqueException(String message) {
        super(message);
    }
}
