package com.example.bankservice.service.impl;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.CardHolderResponse;
import com.example.bankservice.exception.CardNumberUniqueException;
import com.example.bankservice.mapper.BankCardMapper;
import com.example.bankservice.model.BankCard;
import com.example.bankservice.model.enums.CardHolder;
import com.example.bankservice.repository.BankCardRepository;
import com.example.bankservice.service.BankCardService;
import com.example.bankservice.webClient.DriverWebClient;
import com.example.bankservice.webClient.PassengerWebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankCardServiceImpl implements BankCardService {
    private static final String CARD_NUMBER_EXIST = "Card with number '%s' already exist";
    private final BankCardRepository bankCardRepository;
    private final BankCardMapper bankCardMapper;
    private final PassengerWebClient passengerWebClient;
    private final DriverWebClient driverWebClient;

    @Override
    public BankCardResponse createBankCard(BankCardRequest bankCardRequest) {
        String cardNumber = bankCardRequest.getNumber();
        bankCardRepository.findBankCardByNumber(cardNumber)
                .ifPresent(bankCard -> {
                    throw new CardNumberUniqueException(String.format(CARD_NUMBER_EXIST, bankCard.getNumber()));
                });
        CardHolderResponse user;
        if(bankCardRequest.getCardHolder().equals(CardHolder.PASSENGER)) {
            user = passengerWebClient.getPassenger(bankCardRequest.getCardHolderId());
        } else {
            user = driverWebClient.getDriver(bankCardRequest.getCardHolderId());
        }
        BankCard newBankCard = bankCardMapper.mapBankCardRequestToBankCard(bankCardRequest);
        newBankCard = bankCardRepository.save(newBankCard);
        return bankCardMapper.mapBankCardToBankCardResponse(newBankCard, user);
    }
}