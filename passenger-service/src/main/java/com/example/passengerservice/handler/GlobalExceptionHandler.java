package com.example.passengerservice.handler;

import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.exception.PassengerRatingNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = PassengerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePassengerException(PassengerNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(value = PassengerRatingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePassengerRatingException(PassengerRatingNotFoundException ex) {
        return ex.getMessage();
    }
}