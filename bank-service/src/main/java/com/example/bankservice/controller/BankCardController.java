package com.example.bankservice.controller;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.model.enums.BankUser;
import com.example.bankservice.service.BankCardService;
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
@RequestMapping("/bank/card")
public class BankCardController {
    private final BankCardService bankCardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankCardResponse createBankCard(@Valid @RequestBody BankCardRequest bankCardRequest) {
        return bankCardService.createBankCard(bankCardRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BankCardResponse editBankCard(@PathVariable("id") Long id,
                                         @Valid @RequestBody UpdateBankCardRequest updateBankCardRequest) {
        return bankCardService.editBankCard(id, updateBankCardRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBankCard(@PathVariable("id") Long id) {
        bankCardService.deleteBankCard(id);
    }

    @DeleteMapping("/user/{bankUserId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBankUserCards(@PathVariable("bankUserId") Long bankUserId,
                                    @RequestParam BankUser bankUser) {
        bankCardService.deleteBankUserCards(bankUserId, bankUser);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BankCardResponse getBankCardById(@PathVariable("id") Long id) {
        return bankCardService.getBankCardById(id);
    }

    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BankCardPageResponse getBankUserCards(@PathVariable("id") Long bankUserId,
                                                 @RequestParam BankUser bankUser,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(defaultValue = "id") String sortBy) {
        return bankCardService.getBankCardsByBankUser(bankUserId, bankUser, page, size, sortBy);
    }

    @PutMapping("/default/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BankCardResponse makeBankCardDefault(@PathVariable("id") Long id) {
        return bankCardService.makeBankCardDefault(id);
    }

    @GetMapping("/user/{id}/default")
    @ResponseStatus(HttpStatus.OK)
    public BankCardResponse getDefaultBankCard(@PathVariable("id") Long bankUserId,
                                               @RequestParam BankUser bankUser) {
        return bankCardService.getDefaultBankCard(bankUserId, bankUser);
    }

    @PutMapping("/{id}/refill")
    @ResponseStatus(HttpStatus.OK)
    public BankCardResponse refillBankCard(@PathVariable("id") Long id,
                                           @RequestBody RefillRequest refillRequest) {
        return bankCardService.refillBankCard(id, refillRequest);
    }

    @GetMapping("/{id}/balance")
    @ResponseStatus(HttpStatus.OK)
    public BalanceResponse getBankCardBalance(@PathVariable("id") Long id) {
        return bankCardService.getBankCardBalance(id);
    }
}