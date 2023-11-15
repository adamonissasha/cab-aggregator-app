package com.example.ridesservice.handler;

import com.example.ridesservice.exception.IncorrectDateException;
import com.example.ridesservice.exception.PromoCodeAlreadyExistsException;
import com.example.ridesservice.exception.PromoCodeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = PromoCodeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePromoCodeNotFoundException(PromoCodeNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(value = PromoCodeAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePromoCodeAlreadyExistsException(PromoCodeAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(value = IncorrectDateException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleIncorrectDateException(IncorrectDateException ex) {
        return ex.getMessage();
    }
}