package com.example.bankservice.service;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.TopUpCardRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.response.BankCardBalanceResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.model.enums.CardHolder;

public interface BankCardService {
    BankCardResponse createBankCard(BankCardRequest bankCardRequest);

    BankCardResponse editBankCard(Long id, UpdateBankCardRequest updateBankCardRequest);

    void deleteBankCard(Long id);

    BankCardResponse getBankCardById(Long id);

    BankCardPageResponse getBankCardsByCardHolder(Long cardHolderId, CardHolder cardHolder, int page, int size, String sortBy);

    BankCardResponse makeBankCardDefault(Long id);

    BankCardResponse getDefaultBankCard(Long cardHolderId, CardHolder cardHolder);

    BankCardResponse topUpBankCard(Long id, TopUpCardRequest topUpCardRequest);

    BankCardBalanceResponse getBankCardBalance(Long id);
}
