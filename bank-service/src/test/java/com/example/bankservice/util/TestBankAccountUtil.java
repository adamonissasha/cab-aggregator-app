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
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestBankAccountUtil {
    static Long FIRST_BANK_ACCOUNT_ID = 98L;
    static Long SECOND_BANK_ACCOUNT_ID = 99L;
    static Long INVALID_BANK_ACCOUNT_ID = 199L;
    static String FIRST_ACCOUNT_NUMBER = "ojvcih2vf345vw";
    static String SECOND_ACCOUNT_NUMBER = "sdhfjrkwnm4kl3";
    static String INVALID_ACCOUNT_NUMBER = "ojvc5vw";
    static String UNIQUE_EXISTING_ACCOUNT_NUMBER = "1726fh5ke829sk";
    static BigDecimal FIRST_ACCOUNT_BALANCE = BigDecimal.valueOf(250.6);
    static BigDecimal SECOND_ACCOUNT_BALANCE = BigDecimal.valueOf(123.3);
    static Long FIRST_BANK_DRIVER_ID = 1L;
    static Long SECOND_BANK_DRIVER_ID = 2L;
    static String BANK_USER_FIRST_NAME = "Sasha";
    static String BANK_USER_LAST_NAME = "Adamonis";
    static String BANK_USER_EMAIL = "sasha@gmail.com";
    static String FIRST_BANK_USER_PHONE_NUMBER = "+375099490457";
    static String SECOND_BANK_USER_PHONE_NUMBER = "+375099490557";
    static BigDecimal WITHDRAWAL_SUM = BigDecimal.valueOf(100.5);
    static BigDecimal LARGE_WITHDRAWAL_SUM = BigDecimal.valueOf(299);
    static BigDecimal OUTSIDE_BORDER_WITHDRAWAL_SUM = BigDecimal.valueOf(500);
    private static final BigDecimal DRIVER_PERCENT = BigDecimal.valueOf(0.7);
    static BigDecimal REFILL_SUM = BigDecimal.valueOf(50);
    static int PAGE_NUMBER = 0;
    static int PAGE_SIZE = 2;
    static String CORRECT_SORT_FIELD = "id";
    static String INCORRECT_SORT_FIELD = "ida";
    static String BANK_ACCOUNT_NUMBER_EXIST = "Bank account with number '%s' already exist";
    static String DRIVER_ALREADY_HAS_ACCOUNT = "Driver with id '%s' already has bank account";
    static String BANK_ACCOUNT_NOT_FOUND = "Bank account with id '%s' not found";
    static String BANK_ACCOUNT_BY_DRIVER_ID_NOT_FOUND = "Driver with id '%s' bank account not found";
    static String WITHDRAWAL_SUM_IS_OUTSIDE = "Withdrawal sum %s isn't included in the range from 30 to 300 BYN";
    static String LARGE_WITHDRAWAL_SUM_MESSAGE = "Withdrawal sum %s exceeds bank account balance";
    static String BANK_ACCOUNT_NUMBER_FORMAT = "Bank account number must consist of 14 letters and numbers";
    static String BANK_ACCOUNT_DRIVER_ID_REQUIRED = "Bank account driver id is required";
    static String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: [id, number, balance, driverId, isActive]";

    public static Long getBankAccountId() {
        return SECOND_BANK_ACCOUNT_ID;
    }

    public static Long getInvalidBankAccountId() {
        return INVALID_BANK_ACCOUNT_ID;
    }

    public static BankAccount getFirstBankAccount() {
        return BankAccount.builder()
                .id(FIRST_BANK_ACCOUNT_ID)
                .number(FIRST_ACCOUNT_NUMBER)
                .balance(FIRST_ACCOUNT_BALANCE)
                .driverId(FIRST_BANK_DRIVER_ID)
                .isActive(true)
                .build();
    }

    public static BankAccount getSecondBankAccount() {
        return BankAccount.builder()
                .id(SECOND_BANK_ACCOUNT_ID)
                .number(SECOND_ACCOUNT_NUMBER)
                .balance(SECOND_ACCOUNT_BALANCE)
                .driverId(SECOND_BANK_DRIVER_ID)
                .isActive(true)
                .build();
    }

    public static BankAccountRequest getBankAccountRequest() {
        return BankAccountRequest.builder()
                .number(FIRST_ACCOUNT_NUMBER)
                .driverId(FIRST_BANK_DRIVER_ID)
                .build();
    }

    public static BankAccountRequest getBankAccountRequestWithExistingNumber() {
        return BankAccountRequest.builder()
                .number(FIRST_ACCOUNT_NUMBER)
                .driverId(3L)
                .build();
    }

    public static BankAccountRequest getUniqueBankAccountRequest() {
        return BankAccountRequest.builder()
                .number(UNIQUE_EXISTING_ACCOUNT_NUMBER)
                .driverId(FIRST_BANK_DRIVER_ID)
                .build();
    }

    public static BankAccountRequest getCreateBankAccountRequest() {
        return BankAccountRequest.builder()
                .number(UNIQUE_EXISTING_ACCOUNT_NUMBER)
                .driverId(SECOND_BANK_DRIVER_ID)
                .build();
    }

    public static BankAccountRequest getBankAccountRequestWithInvalidData() {
        return BankAccountRequest.builder()
                .number(INVALID_ACCOUNT_NUMBER)
                .build();
    }

    public static BankAccountResponse getFirstBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(FIRST_BANK_ACCOUNT_ID)
                .number(FIRST_ACCOUNT_NUMBER)
                .balance(FIRST_ACCOUNT_BALANCE)
                .driver(getFirstBankUserResponse())
                .isActive(true)
                .build();
    }

    public static BankAccountResponse getSecondBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(SECOND_BANK_ACCOUNT_ID)
                .number(SECOND_ACCOUNT_NUMBER)
                .balance(SECOND_ACCOUNT_BALANCE)
                .driver(getSecondBankUserResponse())
                .isActive(true)
                .build();
    }

    public static BankAccountResponse getRefillBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(FIRST_BANK_ACCOUNT_ID)
                .number(FIRST_ACCOUNT_NUMBER)
                .balance(FIRST_ACCOUNT_BALANCE.add(DRIVER_PERCENT.multiply(REFILL_SUM)))
                .driver(getFirstBankUserResponse())
                .isActive(true)
                .build();
    }

    public static BankUserResponse getFirstBankUserResponse() {
        return BankUserResponse.builder()
                .id(FIRST_BANK_DRIVER_ID)
                .email(BANK_USER_EMAIL)
                .firstName(BANK_USER_FIRST_NAME)
                .lastName(BANK_USER_LAST_NAME)
                .phoneNumber(FIRST_BANK_USER_PHONE_NUMBER)
                .build();
    }

    public static BankUserResponse getSecondBankUserResponse() {
        return BankUserResponse.builder()
                .id(SECOND_BANK_DRIVER_ID)
                .email(BANK_USER_EMAIL)
                .firstName(BANK_USER_FIRST_NAME)
                .lastName(BANK_USER_LAST_NAME)
                .phoneNumber(SECOND_BANK_USER_PHONE_NUMBER)
                .build();
    }

    public static BankAccountResponse getNewBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(SECOND_BANK_ACCOUNT_ID)
                .number(UNIQUE_EXISTING_ACCOUNT_NUMBER)
                .balance(BigDecimal.ZERO)
                .driver(getSecondBankUserResponse())
                .isActive(true)
                .build();
    }

    public static WithdrawalRequest getWithdrawalRequest() {
        return WithdrawalRequest.builder()
                .sum(WITHDRAWAL_SUM)
                .build();
    }

    public static WithdrawalRequest getLargeWithdrawalRequest() {
        return WithdrawalRequest.builder()
                .sum(LARGE_WITHDRAWAL_SUM)
                .build();
    }

    public static WithdrawalRequest getOutsideBorderWithdrawalRequest() {
        return WithdrawalRequest.builder()
                .sum(OUTSIDE_BORDER_WITHDRAWAL_SUM)
                .build();
    }

    public static BalanceResponse getBalanceResponse() {
        return BalanceResponse.builder()
                .balance(SECOND_ACCOUNT_BALANCE)
                .build();
    }

    public static BankAccountPageResponse getBankAccountPageResponse() {
        return BankAccountPageResponse.builder()
                .bankAccounts(List.of(getFirstBankAccountResponse(), getSecondBankAccountResponse()))
                .currentPage(PAGE_NUMBER)
                .pageSize(PAGE_SIZE)
                .totalPages(1)
                .totalElements(2)
                .build();
    }

    public static BankAccountResponse getWithdrawalBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(SECOND_BANK_ACCOUNT_ID)
                .number(SECOND_ACCOUNT_NUMBER)
                .balance(SECOND_ACCOUNT_BALANCE.subtract(WITHDRAWAL_SUM))
                .driver(getSecondBankUserResponse())
                .isActive(true)
                .build();
    }

    public static RefillRequest getRefillRequest() {
        return RefillRequest.builder()
                .bankUserId(FIRST_BANK_DRIVER_ID)
                .sum(REFILL_SUM)
                .build();
    }

    public static RefillRequest getInvalidRefillRequest() {
        return RefillRequest.builder()
                .bankUserId(INVALID_BANK_ACCOUNT_ID)
                .sum(REFILL_SUM)
                .build();
    }

    public static int getPageNumber() {
        return PAGE_NUMBER;
    }

    public static int getPageSize() {
        return PAGE_SIZE;
    }

    public static String getCorrectSortField() {
        return CORRECT_SORT_FIELD;
    }

    public static String getIncorrectSortField() {
        return INCORRECT_SORT_FIELD;
    }

    public static List<BankAccount> getBankAccounts() {
        return List.of(getFirstBankAccount(), getSecondBankAccount());
    }

    public static List<BankAccountResponse> getBankAccountResponses() {
        return List.of(getFirstBankAccountResponse(), getSecondBankAccountResponse());
    }

    public static ExceptionResponse getBankAccountNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(BANK_ACCOUNT_NOT_FOUND, INVALID_BANK_ACCOUNT_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public static ExceptionResponse getBankAccountNumberExistsExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(BANK_ACCOUNT_NUMBER_EXIST, FIRST_ACCOUNT_NUMBER))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public static ExceptionResponse getDriverAlreadyHasAccountExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(DRIVER_ALREADY_HAS_ACCOUNT, FIRST_BANK_DRIVER_ID))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public static ExceptionResponse getDriverNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(BANK_ACCOUNT_BY_DRIVER_ID_NOT_FOUND, INVALID_BANK_ACCOUNT_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public static ValidationErrorResponse getValidationErrorResponse() {
        List<String> errors = new ArrayList<>();
        errors.add(BANK_ACCOUNT_NUMBER_FORMAT);
        errors.add(BANK_ACCOUNT_DRIVER_ID_REQUIRED);
        Collections.sort(errors);

        return ValidationErrorResponse.builder()
                .errors(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public static ExceptionResponse getBalanceExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(LARGE_WITHDRAWAL_SUM_MESSAGE, LARGE_WITHDRAWAL_SUM))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public static ExceptionResponse getOutsideBorderExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(WITHDRAWAL_SUM_IS_OUTSIDE, OUTSIDE_BORDER_WITHDRAWAL_SUM))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public static ExceptionResponse getIncorrectFieldExceptionResponse() {
        return ExceptionResponse.builder()
                .message(INCORRECT_FIELDS)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}