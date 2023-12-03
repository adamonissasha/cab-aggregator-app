package com.example.bankservice.controller;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.service.BankCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}