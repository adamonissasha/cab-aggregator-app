package com.example.bankservice.service;

import com.example.bankservice.dto.request.BankAccountRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankAccountPageResponse;
import com.example.bankservice.dto.response.BankAccountResponse;

public interface BankAccountService {

    BankAccountResponse createBankAccount(BankAccountRequest bankAccountRequest);

    void deleteBankAccount(Long driverId);

    BankAccountResponse getBankAccountById(Long id);

    BankAccountPageResponse getAllActiveBankAccounts(int page, int size, String sortBy);

    BankAccountPageResponse getAllBankAccounts(int page, int size, String sortBy);

    BankAccountResponse refillBankAccount(RefillRequest refillRequest);

    BalanceResponse getBankAccountBalance(Long id);

    BankAccountResponse withdrawalFromBankAccount(Long id, WithdrawalRequest withdrawalRequest);
}
