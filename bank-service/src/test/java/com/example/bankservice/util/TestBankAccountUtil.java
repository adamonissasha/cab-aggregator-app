package com.example.bankservice.util;

import com.example.bankservice.dto.request.BankAccountRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankAccountPageResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.dto.response.ValidationErrorResponse;
import com.example.bankservice.model.BankAccount;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class TestBankAccountUtil {
    private final Long FIRST_BANK_ACCOUNT_ID = 98L;
    private final Long SECOND_BANK_ACCOUNT_ID = 99L;
    private final Long INVALID_BANK_ACCOUNT_ID = 199L;
    private final String FIRST_ACCOUNT_NUMBER = "ojvcih2vf345vw";
    private final String SECOND_ACCOUNT_NUMBER = "sdhfjrkwnm4kl3";
    private final String INVALID_ACCOUNT_NUMBER = "ojvc5vw";
    private final String UNIQUE_EXISTING_ACCOUNT_NUMBER = "1726fh5ke829sk";
    private final BigDecimal FIRST_ACCOUNT_BALANCE = BigDecimal.valueOf(250.6);
    private final BigDecimal SECOND_ACCOUNT_BALANCE = BigDecimal.valueOf(123.3);
    private final Long FIRST_BANK_DRIVER_ID = 1L;
    private final Long SECOND_BANK_DRIVER_ID = 2L;
    private final String BANK_USER_FIRST_NAME = "Sasha";
    private final String BANK_USER_LAST_NAME = "Adamonis";
    private final String BANK_USER_EMAIL = "sasha@gmail.com";
    private final String FIRST_BANK_USER_PHONE_NUMBER = "+375099490457";
    private final String SECOND_BANK_USER_PHONE_NUMBER = "+375099490557";
    private final BigDecimal WITHDRAWAL_SUM = BigDecimal.valueOf(100.5);
    private final BigDecimal LARGE_WITHDRAWAL_SUM = BigDecimal.valueOf(299);
    private final BigDecimal OUTSIDE_BORDER_WITHDRAWAL_SUM = BigDecimal.valueOf(500);
    private final BigDecimal DRIVER_PERCENT = BigDecimal.valueOf(0.7);
    private final BigDecimal REFILL_SUM = BigDecimal.valueOf(50);
    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 2;
    private final String CORRECT_SORT_FIELD = "id";
    private final String INCORRECT_SORT_FIELD = "ida";
    private final String BANK_ACCOUNT_NUMBER_EXIST = "Bank account with number '%s' already exist";
    private final String DRIVER_ALREADY_HAS_ACCOUNT = "Driver with id '%s' already has bank account";
    private final String BANK_ACCOUNT_NOT_FOUND = "Bank account with id '%s' not found";
    private final String BANK_ACCOUNT_BY_DRIVER_ID_NOT_FOUND = "Driver with id '%s' bank account not found";
    private final String WITHDRAWAL_SUM_IS_OUTSIDE = "Withdrawal sum %s isn't included in the range from 30 to 300 BYN";
    private final String LARGE_WITHDRAWAL_SUM_MESSAGE = "Withdrawal sum %s exceeds bank account balance";
    private final String BANK_ACCOUNT_NUMBER_FORMAT = "Bank account number must consist of 14 letters and numbers";
    private final String BANK_ACCOUNT_DRIVER_ID_REQUIRED = "Bank account driver id is required";
    private final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: [id, number, balance, driverId, isActive]";

    public Long getBankAccountId() {
        return SECOND_BANK_ACCOUNT_ID;
    }

    public Long getInvalidBankAccountId() {
        return INVALID_BANK_ACCOUNT_ID;
    }

    public BankAccount getFirstBankAccount() {
        return BankAccount.builder()
                .id(FIRST_BANK_ACCOUNT_ID)
                .number(FIRST_ACCOUNT_NUMBER)
                .balance(FIRST_ACCOUNT_BALANCE)
                .driverId(FIRST_BANK_DRIVER_ID)
                .isActive(true)
                .build();
    }

    public BankAccount getSecondBankAccount() {
        return BankAccount.builder()
                .id(SECOND_BANK_ACCOUNT_ID)
                .number(SECOND_ACCOUNT_NUMBER)
                .balance(SECOND_ACCOUNT_BALANCE)
                .driverId(SECOND_BANK_DRIVER_ID)
                .isActive(true)
                .build();
    }

    public BankAccountRequest getBankAccountRequest() {
        return BankAccountRequest.builder()
                .number(FIRST_ACCOUNT_NUMBER)
                .driverId(FIRST_BANK_DRIVER_ID)
                .build();
    }

    public BankAccountRequest getBankAccountRequestWithExistingNumber() {
        return BankAccountRequest.builder()
                .number(FIRST_ACCOUNT_NUMBER)
                .driverId(3L)
                .build();
    }

    public BankAccountRequest getUniqueBankAccountRequest() {
        return BankAccountRequest.builder()
                .number(UNIQUE_EXISTING_ACCOUNT_NUMBER)
                .driverId(FIRST_BANK_DRIVER_ID)
                .build();
    }

    public BankAccountRequest getCreateBankAccountRequest() {
        return BankAccountRequest.builder()
                .number(UNIQUE_EXISTING_ACCOUNT_NUMBER)
                .driverId(SECOND_BANK_DRIVER_ID)
                .build();
    }

    public BankAccountRequest getBankAccountRequestWithInvalidData() {
        return BankAccountRequest.builder()
                .number(INVALID_ACCOUNT_NUMBER)
                .build();
    }

    public BankAccountResponse getFirstBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(FIRST_BANK_ACCOUNT_ID)
                .number(FIRST_ACCOUNT_NUMBER)
                .balance(FIRST_ACCOUNT_BALANCE)
                .driver(getFirstBankUserResponse())
                .isActive(true)
                .build();
    }

    public BankAccountResponse getSecondBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(SECOND_BANK_ACCOUNT_ID)
                .number(SECOND_ACCOUNT_NUMBER)
                .balance(SECOND_ACCOUNT_BALANCE)
                .driver(getSecondBankUserResponse())
                .isActive(true)
                .build();
    }

    public BankAccountResponse getRefillBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(FIRST_BANK_ACCOUNT_ID)
                .number(FIRST_ACCOUNT_NUMBER)
                .balance(FIRST_ACCOUNT_BALANCE.add(DRIVER_PERCENT.multiply(REFILL_SUM)))
                .driver(getFirstBankUserResponse())
                .isActive(true)
                .build();
    }

    public BankUserResponse getFirstBankUserResponse() {
        return BankUserResponse.builder()
                .id(FIRST_BANK_DRIVER_ID)
                .email(BANK_USER_EMAIL)
                .firstName(BANK_USER_FIRST_NAME)
                .lastName(BANK_USER_LAST_NAME)
                .phoneNumber(FIRST_BANK_USER_PHONE_NUMBER)
                .build();
    }

    public BankUserResponse getSecondBankUserResponse() {
        return BankUserResponse.builder()
                .id(SECOND_BANK_DRIVER_ID)
                .email(BANK_USER_EMAIL)
                .firstName(BANK_USER_FIRST_NAME)
                .lastName(BANK_USER_LAST_NAME)
                .phoneNumber(SECOND_BANK_USER_PHONE_NUMBER)
                .build();
    }

    public BankAccountResponse getNewBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(SECOND_BANK_ACCOUNT_ID)
                .number(UNIQUE_EXISTING_ACCOUNT_NUMBER)
                .balance(BigDecimal.ZERO)
                .driver(getSecondBankUserResponse())
                .isActive(true)
                .build();
    }

    public WithdrawalRequest getWithdrawalRequest() {
        return WithdrawalRequest.builder()
                .sum(WITHDRAWAL_SUM)
                .build();
    }

    public WithdrawalRequest getLargeWithdrawalRequest() {
        return WithdrawalRequest.builder()
                .sum(LARGE_WITHDRAWAL_SUM)
                .build();
    }

    public WithdrawalRequest getOutsideBorderWithdrawalRequest() {
        return WithdrawalRequest.builder()
                .sum(OUTSIDE_BORDER_WITHDRAWAL_SUM)
                .build();
    }

    public BalanceResponse getBalanceResponse() {
        return BalanceResponse.builder()
                .balance(SECOND_ACCOUNT_BALANCE)
                .build();
    }

    public BankAccountPageResponse getBankAccountPageResponse() {
        return BankAccountPageResponse.builder()
                .bankAccounts(List.of(getFirstBankAccountResponse(), getSecondBankAccountResponse()))
                .currentPage(PAGE_NUMBER)
                .pageSize(PAGE_SIZE)
                .totalPages(1)
                .totalElements(2)
                .build();
    }

    public BankAccountResponse getWithdrawalBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(SECOND_BANK_ACCOUNT_ID)
                .number(SECOND_ACCOUNT_NUMBER)
                .balance(SECOND_ACCOUNT_BALANCE.subtract(WITHDRAWAL_SUM))
                .driver(getSecondBankUserResponse())
                .isActive(true)
                .build();
    }

    public RefillRequest getRefillRequest() {
        return RefillRequest.builder()
                .bankUserId(FIRST_BANK_DRIVER_ID)
                .sum(REFILL_SUM)
                .build();
    }

    public RefillRequest getInvalidRefillRequest() {
        return RefillRequest.builder()
                .bankUserId(INVALID_BANK_ACCOUNT_ID)
                .sum(REFILL_SUM)
                .build();
    }

    public int getPageNumber() {
        return PAGE_NUMBER;
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

    public String getCorrectSortField() {
        return CORRECT_SORT_FIELD;
    }

    public String getIncorrectSortField() {
        return INCORRECT_SORT_FIELD;
    }

    public List<BankAccount> getBankAccounts() {
        return List.of(getFirstBankAccount(), getSecondBankAccount());
    }

    public List<BankAccountResponse> getBankAccountResponses() {
        return List.of(getFirstBankAccountResponse(), getSecondBankAccountResponse());
    }

    public ExceptionResponse getBankAccountNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(BANK_ACCOUNT_NOT_FOUND, INVALID_BANK_ACCOUNT_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public ExceptionResponse getBankAccountNumberExistsExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(BANK_ACCOUNT_NUMBER_EXIST, FIRST_ACCOUNT_NUMBER))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public ExceptionResponse getDriverAlreadyHasAccountExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(DRIVER_ALREADY_HAS_ACCOUNT, FIRST_BANK_DRIVER_ID))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public ExceptionResponse getDriverNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(BANK_ACCOUNT_BY_DRIVER_ID_NOT_FOUND, INVALID_BANK_ACCOUNT_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public ValidationErrorResponse getValidationErrorResponse() {
        List<String> errors = new ArrayList<>();
        errors.add(BANK_ACCOUNT_NUMBER_FORMAT);
        errors.add(BANK_ACCOUNT_DRIVER_ID_REQUIRED);
        Collections.sort(errors);

        return ValidationErrorResponse.builder()
                .errors(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public ExceptionResponse getBalanceExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(LARGE_WITHDRAWAL_SUM_MESSAGE, LARGE_WITHDRAWAL_SUM))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public ExceptionResponse getOutsideBorderExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(WITHDRAWAL_SUM_IS_OUTSIDE, OUTSIDE_BORDER_WITHDRAWAL_SUM))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public ExceptionResponse getIncorrectFieldExceptionResponse() {
        return ExceptionResponse.builder()
                .message(INCORRECT_FIELDS)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}