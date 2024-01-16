package com.example.bankservice.handler;

import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.dto.response.ValidationErrorResponse;
import com.example.bankservice.exception.AccountNumberUniqueException;
import com.example.bankservice.exception.BankAccountNotFoundException;
import com.example.bankservice.exception.BankCardBalanceException;
import com.example.bankservice.exception.BankCardNotFoundException;
import com.example.bankservice.exception.CardNumberUniqueException;
import com.example.bankservice.exception.DriverBankAccountException;
import com.example.bankservice.exception.DriverNotFoundException;
import com.example.bankservice.exception.IncorrectFieldNameException;
import com.example.bankservice.exception.PassengerNotFoundException;
import com.example.bankservice.exception.WithdrawalException;
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
    @ExceptionHandler(value = CardNumberUniqueException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleCardNumberUniqueException(CardNumberUniqueException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
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

    @ExceptionHandler(value = BankCardBalanceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleBankCardBalanceException(BankCardBalanceException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
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

    @ExceptionHandler(value = BankCardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleBankCardNotFoundException(BankCardNotFoundException ex) {
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

    @ExceptionHandler(value = AccountNumberUniqueException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleAccountNumberUniqueException(AccountNumberUniqueException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
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

    @ExceptionHandler(value = DriverBankAccountException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleDriverBankAccountException(DriverBankAccountException ex) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(value = WithdrawalException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleWithdrawalException(WithdrawalException ex) {
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