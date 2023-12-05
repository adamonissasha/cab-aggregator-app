package com.example.bankservice.exception;

public class BankCardBalanceException extends RuntimeException {
    public BankCardBalanceException(String message) {
        super(message);
    }
}
