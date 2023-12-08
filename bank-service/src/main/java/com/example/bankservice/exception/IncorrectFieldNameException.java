package com.example.bankservice.exception;

public class IncorrectFieldNameException extends RuntimeException {
    public IncorrectFieldNameException(String message) {
        super(message);
    }
}
