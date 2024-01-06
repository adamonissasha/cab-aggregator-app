package com.example.bankservice.util;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.model.BankAccount;
import com.example.bankservice.model.BankAccountHistory;
import com.example.bankservice.model.enums.Operation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestBankAccountHistoryUtil {
    static Long FIRST_BANK_ACCOUNT_HISTORY_ID = 1L;
    static Long SECOND_BANK_ACCOUNT_HISTORY_ID = 2L;
    static BankAccount BANK_ACCOUNT = TestBankAccountUtil.getFirstBankAccount();
    static LocalDateTime FIRST_OPERATION_DATE_TIME = LocalDateTime.now();
    static LocalDateTime SECOND_OPERATION_DATE_TIME = LocalDateTime.now().plusMinutes(5);
    static Operation FIRST_OPERATION = Operation.REFILL;
    static Operation SECOND_OPERATION = Operation.WITHDRAWAL;
    static BigDecimal FIRST_BANK_ACCOUNT_HISTORY_SUM = BigDecimal.valueOf(32.3);
    static BigDecimal SECOND_BANK_ACCOUNT_HISTORY_SUM = BigDecimal.valueOf(61.5);

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
                .bankAccount(TestBankAccountUtil.getSecondBankAccountResponse())
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
}