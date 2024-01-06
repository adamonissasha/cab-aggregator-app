package com.example.bankservice.util;

import com.example.bankservice.dto.request.BankAccountRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.model.BankAccount;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestBankAccountUtil {
    static Long FIRST_BANK_ACCOUNT_ID = 1L;
    static Long SECOND_BANK_ACCOUNT_ID = 2L;
    static String FIRST_ACCOUNT_NUMBER = "1234123412341234";
    static String SECOND_ACCOUNT_NUMBER = "1234123412341234";
    static BigDecimal FIRST_ACCOUNT_BALANCE = BigDecimal.valueOf(123.4);
    static BigDecimal SECOND_ACCOUNT_BALANCE = BigDecimal.valueOf(250.6);
    static Long FIRST_BANK_DRIVER_ID = 1L;
    static Long SECOND_BANK_DRIVER_ID = 1L;
    static String FIRST_BANK_USER_FIRST_NAME = "Sasha";
    static String FIRST_BANK_USER_LAST_NAME = "Adamonis";
    static String FIRST_BANK_USER_EMAIL = "sasha@gmail.com";
    static String FIRST_BANK_USER_PHONE_NUMBER = "+375291234567";
    static BigDecimal WITHDRAWAL_SUM = BigDecimal.valueOf(100.5);
    static BigDecimal REFILL_SUM = BigDecimal.valueOf(50);
    static int PAGE_NUMBER = 0;
    static int PAGE_SIZE = 2;
    static String SORT_FIELD = "id";

    public static Long getBankAccountId() {
        return FIRST_BANK_ACCOUNT_ID;
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

    public static BankAccountResponse getFirstBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(FIRST_BANK_ACCOUNT_ID)
                .number(FIRST_ACCOUNT_NUMBER)
                .balance(FIRST_ACCOUNT_BALANCE)
                .driver(getBankUserResponse())
                .isActive(true)
                .build();
    }

    public static BankAccountResponse getSecondBankAccountResponse() {
        return BankAccountResponse.builder()
                .id(SECOND_BANK_ACCOUNT_ID)
                .number(SECOND_ACCOUNT_NUMBER)
                .balance(SECOND_ACCOUNT_BALANCE)
                .driver(getBankUserResponse())
                .isActive(true)
                .build();
    }

    public static BankUserResponse getBankUserResponse() {
        return BankUserResponse.builder()
                .id(FIRST_BANK_DRIVER_ID)
                .email(FIRST_BANK_USER_EMAIL)
                .firstName(FIRST_BANK_USER_FIRST_NAME)
                .lastName(FIRST_BANK_USER_LAST_NAME)
                .phoneNumber(FIRST_BANK_USER_PHONE_NUMBER)
                .build();
    }

    public static WithdrawalRequest getWithdrawalRequest() {
        return WithdrawalRequest.builder()
                .sum(WITHDRAWAL_SUM)
                .build();
    }

    public static BalanceResponse getBalanceResponse() {
        return BalanceResponse.builder()
                .balance(FIRST_ACCOUNT_BALANCE)
                .build();
    }

    public static RefillRequest getRefillRequest() {
        return RefillRequest.builder()
                .bankUserId(FIRST_BANK_DRIVER_ID)
                .sum(REFILL_SUM)
                .build();
    }

    public static int getPageNumber() {
        return PAGE_NUMBER;
    }

    public static int getPageSize() {
        return PAGE_SIZE;
    }

    public static String getSortField() {
        return SORT_FIELD;
    }

    public static List<BankAccount> getBankAccounts() {
        return List.of(getFirstBankAccount(), getSecondBankAccount());
    }

    public static List<BankAccountResponse> getBankAccountResponses() {
        return List.of(getFirstBankAccountResponse(), getSecondBankAccountResponse());
    }
}