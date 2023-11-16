package com.example.ridesservice.handler;

import com.example.ridesservice.exception.IncorrectDateException;
import com.example.ridesservice.exception.PromoCodeAlreadyExistsException;
import com.example.ridesservice.exception.PromoCodeNotFoundException;
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

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errorResponse.addValidationError(violation.getMessage());
        }
        return errorResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errorResponse.addValidationError(fieldError.getDefaultMessage());
        }
        return errorResponse;
    }
}