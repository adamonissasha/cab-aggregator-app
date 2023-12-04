package com.example.bankservice.mapper;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.exception.BankAccountNotFoundException;
import com.example.bankservice.model.BankAccount;
import com.example.bankservice.model.BankAccountHistory;
import com.example.bankservice.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BankAccountHistoryMapper {
    private final ModelMapper modelMapper;
    private final BankAccountRepository bankAccountRepository;
    private static final String BANK_ACCOUNT_NOT_FOUND = "Bank account with id '%s' not found";

    public BankAccountHistory mapBankAccountHistoryRequestToBankAccountHistory(Long id, BankAccountHistoryRequest bankAccountHistoryRequest) {
        BankAccountHistory bankAccountHistory = modelMapper.map(bankAccountHistoryRequest, BankAccountHistory.class);
        bankAccountHistory.setId(null);
        bankAccountHistory.setOperationDateTime(LocalDateTime.now());
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException(String.format(BANK_ACCOUNT_NOT_FOUND, id)));
        bankAccountHistory.setBankAccount(bankAccount);
        return bankAccountHistory;
    }

    public BankAccountHistoryResponse mapBankAccountHistoryToBankAccountHistoryResponse(
            BankAccountHistory bankAccountHistory, BankAccountResponse bankAccount) {
        BankAccountHistoryResponse bankAccountHistoryResponse =
                modelMapper.map(bankAccountHistory, BankAccountHistoryResponse.class);
        bankAccountHistoryResponse.setBankAccount(bankAccount);
        return bankAccountHistoryResponse;
    }
}
