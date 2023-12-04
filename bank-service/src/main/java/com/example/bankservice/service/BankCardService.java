package com.example.bankservice.service;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.model.enums.BankUser;

public interface BankCardService {
    BankCardResponse createBankCard(BankCardRequest bankCardRequest);

    BankCardResponse editBankCard(Long id, UpdateBankCardRequest updateBankCardRequest);

    void deleteBankCard(Long id);

    BankCardResponse getBankCardById(Long id);

    BankCardPageResponse getBankCardsByBankUser(Long bankUserId, BankUser bankUser, int page, int size, String sortBy);

    BankCardResponse makeBankCardDefault(Long id);

    BankCardResponse getDefaultBankCard(Long bankUserId, BankUser bankUser);

    BankCardResponse refillBankCard(Long id, RefillRequest refillRequest);

    BalanceResponse getBankCardBalance(Long id);

    void deleteBankUserCards(Long bankUserId, BankUser bankUser);
}
