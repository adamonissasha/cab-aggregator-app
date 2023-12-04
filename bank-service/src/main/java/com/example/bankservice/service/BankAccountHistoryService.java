package com.example.bankservice.service;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryPageResponse;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;

import java.time.LocalDateTime;

public interface BankAccountHistoryService {

    BankAccountHistoryResponse createBankAccountHistoryRecord(Long id, BankAccountHistoryRequest bankAccountHistoryRequest);

    LocalDateTime getLastWithdrawalDate(Long id);

    BankAccountHistoryPageResponse getBankAccountHistory(Long id, int page, int size, String sortBy);
}
