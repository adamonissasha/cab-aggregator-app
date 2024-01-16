package com.example.ridesservice.handler;

import com.example.ridesservice.dto.response.ExceptionResponse;
import com.example.ridesservice.dto.response.ValidationErrorResponse;
import com.example.ridesservice.exception.IncorrectDateException;
import com.example.ridesservice.exception.IncorrectFieldNameException;
import com.example.ridesservice.exception.IncorrectPaymentMethodException;
import com.example.ridesservice.exception.PaymentMethodException;
import com.example.ridesservice.exception.PromoCodeAlreadyExistsException;
import com.example.ridesservice.exception.PromoCodeNotFoundException;
import com.example.ridesservice.exception.RideNotFoundException;
import com.example.ridesservice.exception.RideStatusException;
import com.example.ridesservice.exception.bank.BankAccountNotFoundException;
import com.example.ridesservice.exception.bank.BankCardBalanceException;
import com.example.ridesservice.exception.driver.CarNotFoundException;
import com.example.ridesservice.exception.driver.DriverNotFoundException;
import com.example.ridesservice.exception.driver.FreeDriverNotFoundException;
import com.example.ridesservice.exception.passenger.PassengerException;
import com.example.ridesservice.exception.passenger.PassengerNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = PromoCodeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handlePromoCodeNotFoundException(PromoCodeNotFoundException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = DriverNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleDriverNotFoundException(DriverNotFoundException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = FreeDriverNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleFreeDriverNotFoundException(FreeDriverNotFoundException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = BankCardBalanceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleBankCardBalanceException(BankCardBalanceException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = PaymentMethodException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handlePaymentMethodException(PaymentMethodException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = CarNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleCarNotFoundException(CarNotFoundException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = PromoCodeAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handlePromoCodeAlreadyExistsException(PromoCodeAlreadyExistsException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = PassengerException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handlePassengerException(PassengerException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = IncorrectDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleIncorrectDateException(IncorrectDateException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = RideStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleRideStatusException(RideStatusException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = IncorrectPaymentMethodException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleIncorrectPaymentMethodException(IncorrectPaymentMethodException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
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

    @ExceptionHandler(value = RideNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleRideNotFoundException(RideNotFoundException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = PassengerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handlePassengerNotFoundException(PassengerNotFoundException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = BankAccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleBankAccountNotFoundException(BankAccountNotFoundException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
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
}