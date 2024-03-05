package com.example.driverservice.handler;

import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.dto.response.ValidationErrorResponse;
import com.example.driverservice.exception.CarNotFoundException;
import com.example.driverservice.exception.CarNumberUniqueException;
import com.example.driverservice.exception.DriverNotFoundException;
import com.example.driverservice.exception.DriverStatusException;
import com.example.driverservice.exception.IncorrectFieldNameException;
import com.example.driverservice.exception.PhoneNumberUniqueException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = DriverNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleDriverNotFoundException(DriverNotFoundException ex) {
        log.error(ex.getMessage());

        return ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = DriverStatusException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleDriverStatusException(DriverStatusException ex) {
        log.error(ex.getMessage());

        return ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = CarNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleCarNotFoundException(CarNotFoundException ex) {
        log.error(ex.getMessage());

        return ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = IncorrectFieldNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleIncorrectFieldNameException(IncorrectFieldNameException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = PhoneNumberUniqueException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handlePhoneNumberUniqueException(PhoneNumberUniqueException ex) {
        log.error(ex.getMessage());

        return ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = CarNumberUniqueException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleCarNumberUniqueException(CarNumberUniqueException ex) {
        log.error(ex.getMessage());

        return ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        return ValidationErrorResponse.builder()
                .errors(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        List<String> errors = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .sorted()
                .toList();

        return ValidationErrorResponse.builder()
                .errors(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
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