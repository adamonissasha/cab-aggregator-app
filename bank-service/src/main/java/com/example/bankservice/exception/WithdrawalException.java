package com.example.bankservice.exception;

public class WithdrawalException extends RuntimeException {
    public WithdrawalException(String message) {
        super(message);
    }
}
