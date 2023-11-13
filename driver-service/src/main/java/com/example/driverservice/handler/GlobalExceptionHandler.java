package com.example.driverservice.handler;

import com.example.driverservice.exception.CarNotFoundException;
import com.example.driverservice.exception.CarNumberUniqueException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = CarNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCarException(CarNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(value = CarNumberUniqueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleCarNumberUniqueException(CarNumberUniqueException ex) {
        return ex.getMessage();
    }
}