package com.example.bankservice.service;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.response.BankCardResponse;

public interface BankCardService {
    BankCardResponse createBankCard(BankCardRequest bankCardRequest);
}
