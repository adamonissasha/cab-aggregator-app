package com.example.bankservice.exception;

public class AccountNumberUniqueException extends RuntimeException {
    public AccountNumberUniqueException(String message) {
        super(message);
    }
}
