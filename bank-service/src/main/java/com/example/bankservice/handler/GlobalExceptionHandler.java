package com.example.bankservice.handler;

import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.exception.CardNumberUniqueException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CardNumberUniqueException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleCardNumberUniqueException(CardNumberUniqueException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
    }
}