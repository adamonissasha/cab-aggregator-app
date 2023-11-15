package com.example.passengerservice.handler;

import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.exception.IncorrectFieldNameException;
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
    public ExceptionResponse handlePassengerException(PassengerNotFoundException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = PassengerRatingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handlePassengerRatingException(PassengerRatingNotFoundException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
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
    public ExceptionResponse handlePhoneNumberUniqueException(PhoneNumberUniqueException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
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

    @ExceptionHandler(value = IncorrectFieldNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleIncorrectFieldNameException(IncorrectFieldNameException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
    }
}