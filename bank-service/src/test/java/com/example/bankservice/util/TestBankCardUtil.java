package com.example.bankservice.util;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.dto.response.ValidationErrorResponse;
import com.example.bankservice.model.BankCard;
import com.example.bankservice.model.enums.BankUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestBankCardUtil {
    static Long FIRST_BANK_CARD_ID = 98L;
    static Long SECOND_BANK_CARD_ID = 99L;
    static Long INVALID_BANK_CARD_ID = 199L;
    static String FIRST_CARD_NUMBER = "1234 1234 1234 1234";
    static String SECOND_CARD_NUMBER = "4321 4321 4321 4321";
    static String UNIQUE_CARD_NUMBER = "4321 4321 1234 4321";
    static String INVALID_CARD_NUMBER = "1234345";
    static String FIRST_CARD_EXPIRY_DATE = "04/06";
    static String SECOND_CARD_EXPIRY_DATE = "01/10";
    static String INVALID_CARD_EXPIRY_DATE = "01-10";
    static String FIRST_CARD_CVV = "364";
    static String SECOND_CARD_CVV = "921";
    static String INVALID_CARD_CVV = "cd3";
    static BigDecimal FIRST_CARD_BALANCE = BigDecimal.valueOf(349.2);
    static BigDecimal SECOND_CARD_BALANCE = BigDecimal.valueOf(136.7);
    static Long FIRST_BANK_USER_ID = 3L;
    static BankUser BANK_USER = BankUser.PASSENGER;
    static String BANK_USER_FIRST_NAME = "Egor";
    static String BANK_USER_LAST_NAME = "Adamonis";
    static String BANK_USER_EMAIL = "a@gmail.com";
    static String BANK_USER_PHONE_NUMBER = "+375207214099";
    static BigDecimal WITHDRAWAL_SUM = BigDecimal.valueOf(100.5);
    static BigDecimal BIG_WITHDRAWAL_SUM = BigDecimal.valueOf(500.6);
    static BigDecimal REFILL_SUM = BigDecimal.valueOf(50);
    static int PAGE_NUMBER = 0;
    static int PAGE_SIZE = 2;
    static String CORRECT_SORT_FIELD = "id";
    static String INCORRECT_SORT_FIELD = "ids";
    static String CARD_NUMBER_EXIST = "Card with number '%s' already exist";
    static String CARD_NOT_FOUND = "Card with id '%s' not found";
    static String DEFAULT_CARD_NOT_FOUND = "%s's with id %s default card not found";
    static String INSUFFICIENT_CARD_BALANCE_TO_PAY = "There is not enough balance money to pay %s BYN " +
            "for the ride. Refill card or change payment method.";
    static String BANK_CARD_NUMBER_FORMAT = "Bank card number must match the format: XXXX XXXX XXXX XXXX";
    static String BANK_CARD_EXPIRY_DATE_FORMAT = "Bank card expiry date must match the format: MM/YY";
    static String BANK_CARD_CVV_FORMAT = "Bank card cvv code must consist of 3 digits";
    static String BANK_USER_ID_REQUIRED = "Bank card user id is required";
    static String BANK_USER_REQUIRED = "Bank card user is required";
    static String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: [id, number, expiryDate, cvv, balance, isDefault, bankUserId, bankUser]";

    public static Long getBankCardId() {
        return FIRST_BANK_CARD_ID;
    }

    public static Long getInvalidBankCardId() {
        return INVALID_BANK_CARD_ID;
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
                .bankUser(BANK_USER)
                .build();
    }

    public static BankCard getSecondBankCard() {
        return BankCard.builder()
                .id(SECOND_BANK_CARD_ID)
                .number(SECOND_CARD_NUMBER)
                .expiryDate(SECOND_CARD_EXPIRY_DATE)
                .cvv(SECOND_CARD_CVV)
                .balance(SECOND_CARD_BALANCE)
                .bankUserId(FIRST_BANK_USER_ID)
                .isDefault(false)
                .bankUser(BANK_USER)
                .build();
    }

    public static BankCardRequest getBankCardRequest() {
        return BankCardRequest.builder()
                .number(FIRST_CARD_NUMBER)
                .expiryDate(FIRST_CARD_EXPIRY_DATE)
                .cvv(FIRST_CARD_CVV)
                .balance(FIRST_CARD_BALANCE)
                .bankUserId(FIRST_BANK_USER_ID)
                .bankUser(BANK_USER)
                .build();
    }

    public static UpdateBankCardRequest getUpdateBankCardRequest() {
        return UpdateBankCardRequest.builder()
                .number(FIRST_CARD_NUMBER)
                .expiryDate(FIRST_CARD_EXPIRY_DATE)
                .cvv(FIRST_CARD_CVV)
                .build();
    }

    public static BankCardRequest getUniqueBankCardRequest() {
        return BankCardRequest.builder()
                .number(UNIQUE_CARD_NUMBER)
                .expiryDate(FIRST_CARD_EXPIRY_DATE)
                .cvv(FIRST_CARD_CVV)
                .balance(FIRST_CARD_BALANCE)
                .bankUserId(FIRST_BANK_USER_ID)
                .bankUser(BANK_USER)
                .build();
    }

    public static BankCardRequest getBankCardRequestWithExistingNumber() {
        return BankCardRequest.builder()
                .number(FIRST_CARD_NUMBER)
                .expiryDate(FIRST_CARD_EXPIRY_DATE)
                .cvv(FIRST_CARD_CVV)
                .balance(FIRST_CARD_BALANCE)
                .bankUserId(FIRST_BANK_USER_ID)
                .bankUser(BANK_USER)
                .build();
    }

    public static BankCardRequest getBankCardRequestWithInvalidData() {
        return BankCardRequest.builder()
                .number(INVALID_CARD_NUMBER)
                .expiryDate(INVALID_CARD_EXPIRY_DATE)
                .cvv(INVALID_CARD_CVV)
                .build();
    }

    public static BankCardResponse getFirstBankCardResponse() {
        return BankCardResponse.builder()
                .id(FIRST_BANK_CARD_ID)
                .number(FIRST_CARD_NUMBER)
                .expiryDate(FIRST_CARD_EXPIRY_DATE)
                .balance(FIRST_CARD_BALANCE)
                .isDefault(true)
                .bankUserRole(BANK_USER.name())
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
                .bankUserRole(BANK_USER.name())
                .bankUser(getBankUserResponse())
                .build();
    }

    public static BankCardResponse getNewBankCardResponse() {
        return BankCardResponse.builder()
                .id(FIRST_BANK_USER_ID)
                .number(UNIQUE_CARD_NUMBER)
                .expiryDate(FIRST_CARD_EXPIRY_DATE)
                .balance(FIRST_CARD_BALANCE)
                .isDefault(false)
                .bankUserRole(BANK_USER.name())
                .bankUser(getBankUserResponse())
                .build();
    }

    public static BankCardResponse getRefillBankCardResponse() {
        return BankCardResponse.builder()
                .id(FIRST_BANK_CARD_ID)
                .number(FIRST_CARD_NUMBER)
                .expiryDate(FIRST_CARD_EXPIRY_DATE)
                .balance(FIRST_CARD_BALANCE.add(REFILL_SUM))
                .isDefault(true)
                .bankUserRole(BANK_USER.name())
                .bankUser(getBankUserResponse())
                .build();
    }

    public static BankCardResponse getWithdrawalBankCardResponse() {
        return BankCardResponse.builder()
                .id(FIRST_BANK_CARD_ID)
                .number(FIRST_CARD_NUMBER)
                .expiryDate(FIRST_CARD_EXPIRY_DATE)
                .balance(FIRST_CARD_BALANCE.subtract(WITHDRAWAL_SUM))
                .isDefault(true)
                .bankUserRole(BANK_USER.name())
                .bankUser(getBankUserResponse())
                .build();
    }

    public static BankUserResponse getBankUserResponse() {
        return BankUserResponse.builder()
                .id(FIRST_BANK_USER_ID)
                .email(BANK_USER_EMAIL)
                .firstName(BANK_USER_FIRST_NAME)
                .lastName(BANK_USER_LAST_NAME)
                .phoneNumber(BANK_USER_PHONE_NUMBER)
                .build();
    }

    public static WithdrawalRequest getWithdrawalRequest() {
        return WithdrawalRequest.builder()
                .sum(WITHDRAWAL_SUM)
                .build();
    }

    public static WithdrawalRequest getBigWithdrawalRequest() {
        return WithdrawalRequest.builder()
                .sum(BIG_WITHDRAWAL_SUM)
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

    public static String getCorrectSortField() {
        return CORRECT_SORT_FIELD;
    }

    public static String getIncorrectSortField() {
        return INCORRECT_SORT_FIELD;
    }

    public static List<BankCard> getBankCards() {
        return List.of(getFirstBankCard(), getSecondBankCard());
    }

    public static List<BankCardResponse> getBankCardResponses() {
        return List.of(getFirstBankCardResponse(), getSecondBankCardResponse());
    }

    public static ExceptionResponse getBankCardNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(CARD_NOT_FOUND, INVALID_BANK_CARD_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public static ExceptionResponse getDefaultBankCardNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(DEFAULT_CARD_NOT_FOUND, BankUser.DRIVER.name(), 9))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public static ExceptionResponse getCardNumberExistsExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(CARD_NUMBER_EXIST, FIRST_CARD_NUMBER))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public static ExceptionResponse getBalanceExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(INSUFFICIENT_CARD_BALANCE_TO_PAY, BIG_WITHDRAWAL_SUM))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public static ValidationErrorResponse getValidationErrorResponse() {
        List<String> errors = new ArrayList<>();
        errors.add(BANK_CARD_NUMBER_FORMAT);
        errors.add(BANK_CARD_EXPIRY_DATE_FORMAT);
        errors.add(BANK_CARD_CVV_FORMAT);
        errors.add(BANK_USER_ID_REQUIRED);
        errors.add(BANK_USER_REQUIRED);
        Collections.sort(errors);

        return ValidationErrorResponse.builder()
                .errors(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public static ValidationErrorResponse getEditValidationErrorResponse() {
        List<String> errors = new ArrayList<>();
        errors.add(BANK_CARD_NUMBER_FORMAT);
        errors.add(BANK_CARD_EXPIRY_DATE_FORMAT);
        errors.add(BANK_CARD_CVV_FORMAT);
        Collections.sort(errors);

        return ValidationErrorResponse.builder()
                .errors(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public static BankCardPageResponse getBankCardPageResponse() {
        return BankCardPageResponse.builder()
                .bankCards(List.of(getFirstBankCardResponse()))
                .currentPage(PAGE_NUMBER)
                .pageSize(PAGE_SIZE)
                .totalPages(1)
                .totalElements(1)
                .build();
    }

    public static ExceptionResponse getIncorrectFieldExceptionResponse() {
        return ExceptionResponse.builder()
                .message(INCORRECT_FIELDS)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}