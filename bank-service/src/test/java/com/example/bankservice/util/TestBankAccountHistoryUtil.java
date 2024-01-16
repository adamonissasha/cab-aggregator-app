package com.example.bankservice.util;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryPageResponse;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.model.BankAccount;
import com.example.bankservice.model.BankAccountHistory;
import com.example.bankservice.model.enums.Operation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestBankAccountHistoryUtil {
    static Long FIRST_BANK_ACCOUNT_HISTORY_ID = 98L;
    static Long SECOND_BANK_ACCOUNT_HISTORY_ID = 99L;
    static BankAccount BANK_ACCOUNT = TestBankAccountUtil.getFirstBankAccount();
    static LocalDateTime FIRST_OPERATION_DATE_TIME = LocalDateTime.now();
    static LocalDateTime SECOND_OPERATION_DATE_TIME = LocalDateTime.now();
    static Operation FIRST_OPERATION = Operation.REFILL;
    static Operation SECOND_OPERATION = Operation.WITHDRAWAL;
    static BigDecimal FIRST_BANK_ACCOUNT_HISTORY_SUM = BigDecimal.valueOf(20.6);
    static BigDecimal SECOND_BANK_ACCOUNT_HISTORY_SUM = BigDecimal.valueOf(13.3);
    static int PAGE_NUMBER = 0;
    static int PAGE_SIZE = 2;
    static String CORRECT_SORT_FIELD = "id";
    static String INCORRECT_SORT_FIELD = "ids";
    static String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: [id, bankAccount, operationDateTime, operation, sum]";

    public static Long getBankAccountId() {
        return BANK_ACCOUNT.getId();
    }

    public static BankAccountHistory getFirstBankAccountHistory() {
        return BankAccountHistory.builder()
                .id(FIRST_BANK_ACCOUNT_HISTORY_ID)
                .bankAccount(BANK_ACCOUNT)
                .operation(FIRST_OPERATION)
                .operationDateTime(FIRST_OPERATION_DATE_TIME)
                .sum(FIRST_BANK_ACCOUNT_HISTORY_SUM)
                .build();
    }

    public static BankAccountHistory getSecondBankAccountHistory() {
        return BankAccountHistory.builder()
                .id(SECOND_BANK_ACCOUNT_HISTORY_ID)
                .bankAccount(BANK_ACCOUNT)
                .operation(SECOND_OPERATION)
                .operationDateTime(SECOND_OPERATION_DATE_TIME)
                .sum(SECOND_BANK_ACCOUNT_HISTORY_SUM)
                .build();
    }

    public static BankAccountHistoryRequest getBankAccountHistoryRequest() {
        return BankAccountHistoryRequest.builder()
                .operation(FIRST_OPERATION)
                .sum(FIRST_BANK_ACCOUNT_HISTORY_SUM)
                .build();
    }

    public static BankAccountHistoryResponse getFirstBankAccountHistoryResponse() {
        return BankAccountHistoryResponse.builder()
                .id(FIRST_BANK_ACCOUNT_HISTORY_ID)
                .bankAccount(TestBankAccountUtil.getFirstBankAccountResponse())
                .operation(FIRST_OPERATION)
                .operationDateTime(FIRST_OPERATION_DATE_TIME)
                .sum(FIRST_BANK_ACCOUNT_HISTORY_SUM)
                .build();
    }

    public static BankAccountHistoryResponse getSecondBankAccountHistoryResponse() {
        return BankAccountHistoryResponse.builder()
                .id(SECOND_BANK_ACCOUNT_HISTORY_ID)
                .bankAccount(TestBankAccountUtil.getFirstBankAccountResponse())
                .operation(SECOND_OPERATION)
                .operationDateTime(SECOND_OPERATION_DATE_TIME)
                .sum(SECOND_BANK_ACCOUNT_HISTORY_SUM)
                .build();
    }

    public static List<BankAccountHistory> getBankAccountHistoryList() {
        return List.of(getFirstBankAccountHistory(), getSecondBankAccountHistory());
    }

    public static List<BankAccountHistoryResponse> getBankAccountHistoryResponses() {
        return List.of(getFirstBankAccountHistoryResponse(), getSecondBankAccountHistoryResponse());
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

    public static BankAccountHistoryPageResponse getBankAccountHistoryPageResponse() {
        return BankAccountHistoryPageResponse.builder()
                .bankAccountHistoryRecords(List.of(getFirstBankAccountHistoryResponse(), getSecondBankAccountHistoryResponse()))
                .currentPage(PAGE_NUMBER)
                .pageSize(PAGE_SIZE)
                .totalPages(1)
                .totalElements(2)
                .build();
    }

    public static ExceptionResponse getIncorrectFieldExceptionResponse() {
        return ExceptionResponse.builder()
                .message(INCORRECT_FIELDS)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}