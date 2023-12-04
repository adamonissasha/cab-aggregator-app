package com.example.bankservice.controller;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.TopUpCardRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.response.BankCardBalanceResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.model.enums.CardHolder;
import com.example.bankservice.service.BankCardService;
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
    public BankCardResponse createBankCard(@RequestBody BankCardRequest bankCardRequest) {
        return bankCardService.createBankCard(bankCardRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BankCardResponse editBankCard(@PathVariable("id") Long id,
                                         @RequestBody UpdateBankCardRequest updateBankCardRequest) {
        return bankCardService.editBankCard(id, updateBankCardRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBankCard(@PathVariable("id") Long id) {
        bankCardService.deleteBankCard(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BankCardResponse getBankCardById(@PathVariable("id") Long id) {
        return bankCardService.getBankCardById(id);
    }

    @GetMapping("/holder/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BankCardPageResponse getCardHolderCards(@PathVariable("id") Long cardHolderId,
                                                   @RequestParam CardHolder cardHolder,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "id") String sortBy) {
        return bankCardService.getBankCardsByCardHolder(cardHolderId, cardHolder, page, size, sortBy);
    }

    @PutMapping("/default/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BankCardResponse makeBankCardDefault(@PathVariable("id") Long id) {
        return bankCardService.makeBankCardDefault(id);
    }

    @GetMapping("/holder/{id}/default")
    @ResponseStatus(HttpStatus.OK)
    public BankCardResponse getDefaultBankCard(@PathVariable("id") Long cardHolderId,
                                               @RequestParam CardHolder cardHolder) {
        return bankCardService.getDefaultBankCard(cardHolderId, cardHolder);
    }

    @PutMapping("/{id}/top-up")
    @ResponseStatus(HttpStatus.OK)
    public BankCardResponse topUpBankCard(@PathVariable("id") Long id,
                                          @RequestBody TopUpCardRequest topUpCardRequest) {
        return bankCardService.topUpBankCard(id, topUpCardRequest);
    }

    @GetMapping("/{id}/balance")
    @ResponseStatus(HttpStatus.OK)
    public BankCardBalanceResponse getBankCardBalance(@PathVariable("id") Long id) {
        return bankCardService.getBankCardBalance(id);
    }
}