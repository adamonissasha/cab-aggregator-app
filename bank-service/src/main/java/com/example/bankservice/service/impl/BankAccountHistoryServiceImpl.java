package com.example.bankservice.service.impl;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryPageResponse;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.mapper.BankAccountHistoryMapper;
import com.example.bankservice.mapper.BankAccountMapper;
import com.example.bankservice.model.BankAccount;
import com.example.bankservice.model.BankAccountHistory;
import com.example.bankservice.model.enums.Operation;
import com.example.bankservice.repository.BankAccountHistoryRepository;
import com.example.bankservice.service.BankAccountHistoryService;
import com.example.bankservice.util.FieldValidator;
import com.example.bankservice.webClient.DriverWebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountHistoryServiceImpl implements BankAccountHistoryService {
    private final BankAccountHistoryRepository bankAccountHistoryRepository;
    private final BankAccountHistoryMapper bankAccountHistoryMapper;
    private final BankAccountMapper bankAccountMapper;
    private final DriverWebClient driverWebClient;
    private final FieldValidator fieldValidator;

    @Override
    public BankAccountHistoryResponse createBankAccountHistoryRecord(Long id, BankAccountHistoryRequest bankAccountHistoryRequest) {
        BankAccountHistory bankAccountHistory =
                bankAccountHistoryMapper.mapBankAccountHistoryRequestToBankAccountHistory(id, bankAccountHistoryRequest);
        bankAccountHistory = bankAccountHistoryRepository.save(bankAccountHistory);
        BankAccount bankAccount = bankAccountHistory.getBankAccount();
        BankUserResponse bankUserResponse = driverWebClient.getDriver(bankAccount.getId());

        BankAccountResponse bankAccountResponse =
                bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccount, bankUserResponse);

        return bankAccountHistoryMapper.mapBankAccountHistoryToBankAccountHistoryResponse(bankAccountHistory,
                bankAccountResponse);
    }

    @Override
    public LocalDateTime getLastWithdrawalDate(Long id) {
        return bankAccountHistoryRepository.findFirstByBankAccountIdAndOperationOrderByOperationDateTimeDesc(id,
                        Operation.WITHDRAWAL)
                .map(BankAccountHistory::getOperationDateTime)
                .orElse(null);
    }

    @Override
    public BankAccountHistoryPageResponse getBankAccountHistory(Long id, int page, int size, String sortBy) {
        fieldValidator.checkSortField(BankAccountHistory.class, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankAccountHistory> bankAccountHistoryPage = bankAccountHistoryRepository.findAllByBankAccountId(id, pageable);
        List<BankAccountHistoryResponse> bankAccountHistoryResponses = bankAccountHistoryPage.getContent()
                .stream()
                .map(bankAccountHistory -> bankAccountHistoryMapper
                        .mapBankAccountHistoryToBankAccountHistoryResponse(bankAccountHistory, bankAccountMapper
                                .mapBankAccountToBankAccountResponse(bankAccountHistory.getBankAccount(),
                                        driverWebClient.getDriver(id))))
                .toList();

        return BankAccountHistoryPageResponse.builder()
                .bankAccountHistoryRecords(bankAccountHistoryResponses)
                .totalPages(bankAccountHistoryPage.getTotalPages())
                .totalElements(bankAccountHistoryPage.getTotalElements())
                .currentPage(bankAccountHistoryPage.getNumber())
                .pageSize(bankAccountHistoryPage.getSize())
                .build();
    }
}