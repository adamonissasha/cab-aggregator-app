package com.example.bankservice.util;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryPageResponse;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.model.BankAccount;
import com.example.bankservice.model.BankAccountHistory;
import com.example.bankservice.model.enums.Operation;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class TestBankAccountHistoryUtil {
    private final Long FIRST_BANK_ACCOUNT_HISTORY_ID = 98L;
    private final Long SECOND_BANK_ACCOUNT_HISTORY_ID = 99L;
    private final BankAccount BANK_ACCOUNT = TestBankAccountUtil.getFirstBankAccount();
    private final LocalDateTime FIRST_OPERATION_DATE_TIME = LocalDateTime.now();
    private final LocalDateTime SECOND_OPERATION_DATE_TIME = LocalDateTime.now();
    private final Operation FIRST_OPERATION = Operation.REFILL;
    private final Operation SECOND_OPERATION = Operation.WITHDRAWAL;
    private final BigDecimal FIRST_BANK_ACCOUNT_HISTORY_SUM = BigDecimal.valueOf(20.6);
    private final BigDecimal SECOND_BANK_ACCOUNT_HISTORY_SUM = BigDecimal.valueOf(13.3);
    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 2;
    private final String CORRECT_SORT_FIELD = "id";
    private final String INCORRECT_SORT_FIELD = "ids";
    private final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: [id, bankAccount, operationDateTime, operation, sum]";

    public Long getBankAccountId() {
        return BANK_ACCOUNT.getId();
    }

    public BankAccountHistory getFirstBankAccountHistory() {
        return BankAccountHistory.builder()
                .id(FIRST_BANK_ACCOUNT_HISTORY_ID)
                .bankAccount(BANK_ACCOUNT)
                .operation(FIRST_OPERATION)
                .operationDateTime(FIRST_OPERATION_DATE_TIME)
                .sum(FIRST_BANK_ACCOUNT_HISTORY_SUM)
                .build();
    }

    public BankAccountHistory getSecondBankAccountHistory() {
        return BankAccountHistory.builder()
                .id(SECOND_BANK_ACCOUNT_HISTORY_ID)
                .bankAccount(BANK_ACCOUNT)
                .operation(SECOND_OPERATION)
                .operationDateTime(SECOND_OPERATION_DATE_TIME)
                .sum(SECOND_BANK_ACCOUNT_HISTORY_SUM)
                .build();
    }

    public BankAccountHistoryRequest getBankAccountHistoryRequest() {
        return BankAccountHistoryRequest.builder()
                .operation(FIRST_OPERATION)
                .sum(FIRST_BANK_ACCOUNT_HISTORY_SUM)
                .build();
    }

    public BankAccountHistoryResponse getFirstBankAccountHistoryResponse() {
        return BankAccountHistoryResponse.builder()
                .id(FIRST_BANK_ACCOUNT_HISTORY_ID)
                .bankAccount(TestBankAccountUtil.getFirstBankAccountResponse())
                .operation(FIRST_OPERATION)
                .operationDateTime(FIRST_OPERATION_DATE_TIME)
                .sum(FIRST_BANK_ACCOUNT_HISTORY_SUM)
                .build();
    }

    public BankAccountHistoryResponse getSecondBankAccountHistoryResponse() {
        return BankAccountHistoryResponse.builder()
                .id(SECOND_BANK_ACCOUNT_HISTORY_ID)
                .bankAccount(TestBankAccountUtil.getFirstBankAccountResponse())
                .operation(SECOND_OPERATION)
                .operationDateTime(SECOND_OPERATION_DATE_TIME)
                .sum(SECOND_BANK_ACCOUNT_HISTORY_SUM)
                .build();
    }

    public List<BankAccountHistory> getBankAccountHistoryList() {
        return List.of(getFirstBankAccountHistory(), getSecondBankAccountHistory());
    }

    public List<BankAccountHistoryResponse> getBankAccountHistoryResponses() {
        return List.of(getFirstBankAccountHistoryResponse(), getSecondBankAccountHistoryResponse());
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

    public BankAccountHistoryPageResponse getBankAccountHistoryPageResponse() {
        return BankAccountHistoryPageResponse.builder()
                .bankAccountHistoryRecords(List.of(getFirstBankAccountHistoryResponse(), getSecondBankAccountHistoryResponse()))
                .currentPage(PAGE_NUMBER)
                .pageSize(PAGE_SIZE)
                .totalPages(1)
                .totalElements(2)
                .build();
    }

    public ExceptionResponse getIncorrectFieldExceptionResponse() {
        return ExceptionResponse.builder()
                .message(INCORRECT_FIELDS)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}