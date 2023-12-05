package com.example.ridesservice.exception.bank;

public class BankCardBalanceException extends RuntimeException {
    public BankCardBalanceException(String message) {
        super(message);
    }
}
