package com.example.ridesservice.exception;

public class IncorrectFieldNameException extends RuntimeException {
    public IncorrectFieldNameException(String message) {
        super(message);
    }
}