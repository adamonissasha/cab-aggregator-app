package com.example.passengerservice.handler;

import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.exception.PassengerRatingNotFoundException;
import com.example.passengerservice.exception.PhoneNumberUniqueException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

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

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errorResponse.addValidationError(violation.getMessage());
        }
        return errorResponse;
    }

    @ExceptionHandler(value = PhoneNumberUniqueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlePhoneNumberUniqueException(PhoneNumberUniqueException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errorResponse.addValidationError(fieldError.getField());
        }
        return errorResponse;
    }
}