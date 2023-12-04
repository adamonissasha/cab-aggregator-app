package com.example.bankservice.service.impl;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.TopUpCardRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.response.BankCardBalanceResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.CardHolderResponse;
import com.example.bankservice.exception.BankCardNotFoundException;
import com.example.bankservice.exception.CardNumberUniqueException;
import com.example.bankservice.exception.IncorrectFieldNameException;
import com.example.bankservice.mapper.BankCardMapper;
import com.example.bankservice.model.BankCard;
import com.example.bankservice.model.enums.CardHolder;
import com.example.bankservice.repository.BankCardRepository;
import com.example.bankservice.service.BankCardService;
import com.example.bankservice.webClient.DriverWebClient;
import com.example.bankservice.webClient.PassengerWebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankCardServiceImpl implements BankCardService {
    private static final String CARD_NUMBER_EXIST = "Card with number '%s' already exist";
    private static final String CARD_NOT_FOUND = "Card with id '%s' not found";
    private static final String DEFAULT_CARD_NOT_FOUND = "%s's with id %s default card not found";
    private static final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: ";
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
        BankCard newBankCard = bankCardMapper.mapBankCardRequestToBankCard(bankCardRequest);
        CardHolderResponse cardHolderResponse = getCardHolder(newBankCard.getCardHolderId(), newBankCard.getCardHolder());
        newBankCard = bankCardRepository.save(newBankCard);
        return bankCardMapper.mapBankCardToBankCardResponse(newBankCard, cardHolderResponse);
    }

    @Override
    public BankCardResponse editBankCard(Long id, UpdateBankCardRequest updateBankCardRequest) {
        BankCard updatedBankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id)));
        String cardNumber = updateBankCardRequest.getNumber();
        bankCardRepository.findBankCardByNumber(cardNumber)
                .ifPresent(bankCard -> {
                    if (!bankCard.getId().equals(id)) {
                        throw new CardNumberUniqueException(String.format(CARD_NUMBER_EXIST, bankCard.getNumber()));
                    }
                });
        updatedBankCard.setNumber(cardNumber);
        updatedBankCard.setExpiryDate(updateBankCardRequest.getExpiryDate());
        updatedBankCard.setCvv(updateBankCardRequest.getCvv());
        updatedBankCard = bankCardRepository.save(updatedBankCard);
        return bankCardMapper.mapBankCardToBankCardResponse(updatedBankCard,
                getCardHolder(updatedBankCard.getCardHolderId(), updatedBankCard.getCardHolder()));
    }

    @Override
    public void deleteBankCard(Long id) {
        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id)));
        bankCardRepository.delete(bankCard);
    }

    @Override
    public BankCardResponse getBankCardById(Long id) {
        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id)));
        return bankCardMapper.mapBankCardToBankCardResponse(bankCard,
                getCardHolder(bankCard.getCardHolderId(), bankCard.getCardHolder()));
    }

    @Override
    public BankCardPageResponse getBankCardsByCardHolder(Long cardHolderId, CardHolder cardHolder, int page, int size, String sortBy) {
        checkSortField(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankCard> bankCardPage = bankCardRepository.findAllByCardHolderIdAndCardHolder(cardHolderId, cardHolder, pageable);
        List<BankCardResponse> bankCardResponses = bankCardPage.getContent()
                .stream()
                .map(bankCard -> bankCardMapper.mapBankCardToBankCardResponse(bankCard,
                        getCardHolder(bankCard.getCardHolderId(), bankCard.getCardHolder())))
                .toList();

        return BankCardPageResponse.builder()
                .bankCards(bankCardResponses)
                .totalPages(bankCardPage.getTotalPages())
                .totalElements(bankCardPage.getTotalElements())
                .currentPage(bankCardPage.getNumber())
                .pageSize(bankCardPage.getSize())
                .build();
    }

    @Override
    public BankCardResponse makeBankCardDefault(Long id) {
        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id)));
        bankCardRepository.findByCardHolderIdAndCardHolderAndIsDefaultTrue(bankCard.getCardHolderId(),
                        bankCard.getCardHolder())
                .ifPresent(previousDefaultCard -> {
                    previousDefaultCard.setIsDefault(false);
                    bankCardRepository.save(previousDefaultCard);
                });
        bankCard.setIsDefault(true);
        bankCardRepository.save(bankCard);
        return bankCardMapper.mapBankCardToBankCardResponse(bankCard,
                getCardHolder(bankCard.getCardHolderId(), bankCard.getCardHolder()));
    }

    @Override
    public BankCardResponse getDefaultBankCard(Long cardHolderId, CardHolder cardHolder) {
        CardHolderResponse cardHolderResponse = getCardHolder(cardHolderId, cardHolder);
        BankCard defaultBankCard = bankCardRepository.findByCardHolderIdAndCardHolderAndIsDefaultTrue(
                        cardHolderId, cardHolder)
                .orElseThrow(() -> new BankCardNotFoundException(String.format(DEFAULT_CARD_NOT_FOUND,
                        cardHolder, cardHolderId)));
        return bankCardMapper.mapBankCardToBankCardResponse(defaultBankCard, cardHolderResponse);
    }

    @Override
    public BankCardResponse topUpBankCard(Long id, TopUpCardRequest topUpCardRequest) {
        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id)));
        bankCard.setBalance(bankCard.getBalance() + topUpCardRequest.getSum());
        bankCard = bankCardRepository.save(bankCard);
        return bankCardMapper.mapBankCardToBankCardResponse(bankCard,
                getCardHolder(bankCard.getCardHolderId(), bankCard.getCardHolder()));
    }

    @Override
    public BankCardBalanceResponse getBankCardBalance(Long id) {
        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id)));
        return BankCardBalanceResponse.builder()
                .balance(bankCard.getBalance())
                .build();
    }

    private CardHolderResponse getCardHolder(Long cardHolderId, CardHolder cardHolder) {
        CardHolderResponse cardHolderResponse;
        if (cardHolder.equals(CardHolder.PASSENGER)) {
            cardHolderResponse = passengerWebClient.getPassenger(cardHolderId);
        } else {
            cardHolderResponse = driverWebClient.getDriver(cardHolderId);
        }
        return cardHolderResponse;
    }

    public void checkSortField(String sortBy) {
        List<String> allowedSortFields = new ArrayList<>();
        getFieldNamesRecursive(BankCard.class, allowedSortFields);
        if (!allowedSortFields.contains(sortBy)) {
            throw new IncorrectFieldNameException(INCORRECT_FIELDS + allowedSortFields);
        }
    }

    private static void getFieldNamesRecursive(Class<?> myClass, List<String> fieldNames) {
        if (myClass != null) {
            Field[] fields = myClass.getDeclaredFields();
            for (Field field : fields) {
                fieldNames.add(field.getName());
            }
            getFieldNamesRecursive(myClass.getSuperclass(), fieldNames);
        }
    }
}