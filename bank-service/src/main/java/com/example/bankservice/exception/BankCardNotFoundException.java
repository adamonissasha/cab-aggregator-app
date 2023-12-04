package com.example.bankservice.exception;

public class BankCardNotFoundException extends RuntimeException {
    public BankCardNotFoundException(String message) {
        super(message);
    }
}
