package com.example.bankservice.controller;

import com.example.bankservice.dto.request.BankAccountRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankAccountPageResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bank/account")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountResponse createBankAccount(@Valid @RequestBody BankAccountRequest bankAccountRequest) {
        return bankAccountService.createBankAccount(bankAccountRequest);
    }

    @DeleteMapping("/{driverId}")
    public void deleteBankAccount(@PathVariable("driverId") Long driverId) {
        bankAccountService.deleteBankAccount(driverId);
    }

    @GetMapping("/{id}")
    public BankAccountResponse getBankAccountById(@PathVariable("id") Long id) {
        return bankAccountService.getBankAccountById(id);
    }

    @GetMapping("/active")
    public BankAccountPageResponse getAllActiveBankAccounts(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "id") String sortBy) {
        return bankAccountService.getAllActiveBankAccounts(page, size, sortBy);
    }

    @GetMapping
    public BankAccountPageResponse getAllBankAccounts(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "id") String sortBy) {
        return bankAccountService.getAllBankAccounts(page, size, sortBy);
    }

    @PutMapping("/refill")
    public BankAccountResponse refillDriverBankAccount(@RequestBody RefillRequest refillRequest) {
        return bankAccountService.refillBankAccount(refillRequest);
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse getBankAccountBalance(@PathVariable("id") Long id) {
        return bankAccountService.getBankAccountBalance(id);
    }

    @PutMapping("/{id}/withdrawal")
    public BankAccountResponse withdrawalFromBankAccount(@PathVariable("id") Long id,
                                                         @RequestBody WithdrawalRequest withdrawalRequest) {
        return bankAccountService.withdrawalFromBankAccount(id, withdrawalRequest);
    }
}