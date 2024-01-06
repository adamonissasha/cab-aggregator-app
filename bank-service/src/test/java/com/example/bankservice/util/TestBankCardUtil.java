package com.example.bankservice.util;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.model.BankCard;
import com.example.bankservice.model.enums.BankUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestBankCardUtil {
    static Long FIRST_BANK_CARD_ID = 1L;
    static Long SECOND_BANK_CARD_ID = 2L;
    static String FIRST_CARD_NUMBER = "1234123412341234";
    static String SECOND_CARD_NUMBER = "1234123412341234";
    static String FIRST_CARD_EXPIRY_DATE = "08/24";
    static String SECOND_CARD_EXPIRY_DATE = "02/25";
    static String FIRST_CARD_CVV = "123";
    static String SECOND_CARD_CVV = "321";
    static BigDecimal FIRST_CARD_BALANCE = BigDecimal.valueOf(123.4);
    static BigDecimal SECOND_CARD_BALANCE = BigDecimal.valueOf(250.6);
    static Long FIRST_BANK_USER_ID = 1L;
    static Long SECOND_BANK_USER_ID = 1L;
    static String FIRST_BANK_USER_FIRST_NAME = "Sasha";
    static String FIRST_BANK_USER_LAST_NAME = "Adamonis";
    static String FIRST_BANK_USER_EMAIL = "sasha@gmail.com";
    static String FIRST_BANK_USER_PHONE_NUMBER = "+375291234567";
    static BigDecimal WITHDRAWAL_SUM = BigDecimal.valueOf(100.5);
    static BigDecimal REFILL_SUM = BigDecimal.valueOf(50);
    static int PAGE_NUMBER = 0;
    static int PAGE_SIZE = 2;
    static String SORT_FIELD = "id";

    public static Long getBankCardId() {
        return FIRST_BANK_CARD_ID;
    }

    public static BankCard getFirstBankCard() {
        return BankCard.builder()
                .id(FIRST_BANK_CARD_ID)
                .number(FIRST_CARD_NUMBER)
                .expiryDate(FIRST_CARD_EXPIRY_DATE)
                .cvv(FIRST_CARD_CVV)
                .balance(FIRST_CARD_BALANCE)
                .bankUserId(FIRST_BANK_USER_ID)
                .isDefault(true)
                .bankUser(BankUser.PASSENGER)
                .build();
    }

    public static BankCard getSecondBankCard() {
        return BankCard.builder()
                .id(SECOND_BANK_CARD_ID)
                .number(SECOND_CARD_NUMBER)
                .expiryDate(SECOND_CARD_EXPIRY_DATE)
                .cvv(SECOND_CARD_CVV)
                .balance(SECOND_CARD_BALANCE)
                .bankUserId(SECOND_BANK_USER_ID)
                .isDefault(false)
                .bankUser(BankUser.PASSENGER)
                .build();
    }

    public static BankCardRequest getBankCardRequest() {
        return BankCardRequest.builder()
                .number(SECOND_CARD_NUMBER)
                .expiryDate(SECOND_CARD_EXPIRY_DATE)
                .cvv(SECOND_CARD_CVV)
                .balance(SECOND_CARD_BALANCE)
                .bankUserId(SECOND_BANK_USER_ID)
                .bankUser(BankUser.PASSENGER)
                .build();
    }

    public static UpdateBankCardRequest getUpdateBankCardRequest() {
        return UpdateBankCardRequest.builder()
                .number(FIRST_CARD_NUMBER)
                .expiryDate(FIRST_CARD_EXPIRY_DATE)
                .cvv(FIRST_CARD_CVV)
                .build();
    }

    public static BankCardResponse getFirstBankCardResponse() {
        return BankCardResponse.builder()
                .id(FIRST_BANK_CARD_ID)
                .number(FIRST_CARD_NUMBER)
                .expiryDate(FIRST_CARD_EXPIRY_DATE)
                .balance(FIRST_CARD_BALANCE)
                .isDefault(true)
                .bankUserRole(BankUser.PASSENGER.name())
                .bankUser(getBankUserResponse())
                .build();
    }

    public static BankCardResponse getSecondBankCardResponse() {
        return BankCardResponse.builder()
                .id(SECOND_BANK_CARD_ID)
                .number(SECOND_CARD_NUMBER)
                .expiryDate(SECOND_CARD_EXPIRY_DATE)
                .balance(SECOND_CARD_BALANCE)
                .isDefault(true)
                .bankUserRole(BankUser.PASSENGER.name())
                .bankUser(getBankUserResponse())
                .build();
    }

    public static BankUserResponse getBankUserResponse () {
        return BankUserResponse.builder()
                .id(FIRST_BANK_USER_ID)
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
                .balance(FIRST_CARD_BALANCE)
                .build();
    }

    public static RefillRequest getRefillRequest() {
        return RefillRequest.builder()
                .bankUserId(FIRST_BANK_USER_ID)
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

    public static List<BankCard> getBankCards() {
        return List.of(getFirstBankCard(), getSecondBankCard());
    }

    public static List<BankCardResponse> getBankCardResponses() {
        return List.of(getFirstBankCardResponse(), getSecondBankCardResponse());
    }
}