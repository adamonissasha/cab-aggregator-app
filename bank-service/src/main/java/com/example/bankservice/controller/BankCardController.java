package com.example.bankservice.controller;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.model.enums.BankUser;
import com.example.bankservice.service.BankCardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/bank/card")
public class BankCardController {
    private final BankCardService bankCardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankCardResponse createBankCard(@Valid @RequestBody BankCardRequest bankCardRequest) {
        return bankCardService.createBankCard(bankCardRequest);
    }

    @PutMapping("/{id}")
    public BankCardResponse editBankCard(@PathVariable("id") Long id,
                                         @Valid @RequestBody UpdateBankCardRequest updateBankCardRequest) {
        return bankCardService.editBankCard(id, updateBankCardRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteBankCard(@PathVariable("id") Long id) {
        bankCardService.deleteBankCard(id);
    }

    @DeleteMapping("/user/{bankUserId}")
    public void deleteBankUserCards(@PathVariable("bankUserId") Long bankUserId,
                                    @RequestParam BankUser bankUser) {
        bankCardService.deleteBankUserCards(bankUserId, bankUser);
    }

    @GetMapping("/{id}")
    public BankCardResponse getBankCardById(@PathVariable("id") Long id) {
        return bankCardService.getBankCardById(id);
    }

    @GetMapping("/user/{id}")
    public BankCardPageResponse getBankUserCards(@PathVariable("id") Long bankUserId,
                                                 @RequestParam BankUser bankUser,
                                                 @RequestParam(defaultValue = "0") @Min(0) int page,
                                                 @RequestParam(defaultValue = "10") @Min(1) int size,
                                                 @RequestParam(defaultValue = "id") String sortBy) {
        return bankCardService.getBankCardsByBankUser(bankUserId, bankUser, page, size, sortBy);
    }

    @PutMapping("/default/{id}")
    public BankCardResponse makeBankCardDefault(@PathVariable("id") Long id) {
        return bankCardService.makeBankCardDefault(id);
    }

    @GetMapping("/user/{id}/default")
    public BankCardResponse getDefaultBankCard(@PathVariable("id") Long bankUserId,
                                               @RequestParam BankUser bankUser) {
        return bankCardService.getDefaultBankCard(bankUserId, bankUser);
    }

    @PutMapping("/{id}/refill")
    public BankCardResponse refillBankCard(@PathVariable("id") Long id,
                                           @RequestBody RefillRequest refillRequest) {
        return bankCardService.refillBankCard(id, refillRequest);
    }

    @PutMapping("/{id}/withdrawal")
    public BankCardResponse withdrawalPaymentFromBankCard(@PathVariable("id") Long id,
                                                          @RequestBody WithdrawalRequest withdrawalRequest) {
        return bankCardService.withdrawalPaymentFromBankCard(id, withdrawalRequest);
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse getBankCardBalance(@PathVariable("id") Long id) {
        return bankCardService.getBankCardBalance(id);
    }
}