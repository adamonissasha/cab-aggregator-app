package com.example.bankservice.service.impl;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.exception.BankCardNotFoundException;
import com.example.bankservice.exception.CardNumberUniqueException;
import com.example.bankservice.exception.IncorrectFieldNameException;
import com.example.bankservice.mapper.BankCardMapper;
import com.example.bankservice.model.BankCard;
import com.example.bankservice.model.enums.BankUser;
import com.example.bankservice.repository.BankCardRepository;
import com.example.bankservice.service.BankCardService;
import com.example.bankservice.webClient.DriverWebClient;
import com.example.bankservice.webClient.PassengerWebClient;
import jakarta.transaction.Transactional;
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
        BankUserResponse bankUserResponse = getBankUser(newBankCard.getBankUserId(), newBankCard.getBankUser());
        newBankCard = bankCardRepository.save(newBankCard);
        return bankCardMapper.mapBankCardToBankCardResponse(newBankCard, bankUserResponse);
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
                getBankUser(updatedBankCard.getBankUserId(), updatedBankCard.getBankUser()));
    }

    @Override
    public void deleteBankCard(Long id) {
        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id)));
        bankCardRepository.delete(bankCard);
    }

    @Override
    @Transactional
    public void deleteBankUserCards(Long bankUserId, BankUser bankUser) {
        bankCardRepository.deleteAllByBankUserIdAndBankUser(bankUserId, bankUser);
    }

    @Override
    public BankCardResponse getBankCardById(Long id) {
        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id)));
        return bankCardMapper.mapBankCardToBankCardResponse(bankCard,
                getBankUser(bankCard.getBankUserId(), bankCard.getBankUser()));
    }

    @Override
    public BankCardPageResponse getBankCardsByBankUser(Long bankUserId, BankUser bankUser, int page, int size, String sortBy) {
        checkSortField(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankCard> bankCardPage = bankCardRepository.findAllByBankUserIdAndBankUser(bankUserId, bankUser, pageable);
        List<BankCardResponse> bankCardResponses = bankCardPage.getContent()
                .stream()
                .map(bankCard -> bankCardMapper.mapBankCardToBankCardResponse(bankCard,
                        getBankUser(bankCard.getBankUserId(), bankCard.getBankUser())))
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
        bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankCard.getBankUserId(),
                        bankCard.getBankUser())
                .ifPresent(previousDefaultCard -> {
                    previousDefaultCard.setIsDefault(false);
                    bankCardRepository.save(previousDefaultCard);
                });
        bankCard.setIsDefault(true);
        bankCardRepository.save(bankCard);
        return bankCardMapper.mapBankCardToBankCardResponse(bankCard,
                getBankUser(bankCard.getBankUserId(), bankCard.getBankUser()));
    }

    @Override
    public BankCardResponse getDefaultBankCard(Long bankUserId, BankUser bankUser) {
        BankUserResponse bankUserResponse = getBankUser(bankUserId, bankUser);
        BankCard defaultBankCard = bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(
                        bankUserId, bankUser)
                .orElseThrow(() -> new BankCardNotFoundException(String.format(DEFAULT_CARD_NOT_FOUND,
                        bankUser, bankUserId)));
        return bankCardMapper.mapBankCardToBankCardResponse(defaultBankCard, bankUserResponse);
    }

    @Override
    public BankCardResponse refillBankCard(Long id, RefillRequest refillRequest) {
        BankCard bankCard;
        if (id == null) {
            BankUser bankUser = BankUser.DRIVER;
            Long bankUserId = refillRequest.getBankUserId();
            bankCard = bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, bankUser)
                    .orElseThrow(() -> new BankCardNotFoundException(String.format(DEFAULT_CARD_NOT_FOUND,
                            bankUser, bankUserId)));
        } else {
            bankCard = bankCardRepository.findById(id)
                    .orElseThrow(() -> new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id)));
        }
        bankCard.setBalance(bankCard.getBalance().add(refillRequest.getSum()));
        bankCard = bankCardRepository.save(bankCard);
        return bankCardMapper.mapBankCardToBankCardResponse(bankCard,
                getBankUser(bankCard.getBankUserId(), bankCard.getBankUser()));
    }

    @Override
    public BalanceResponse getBankCardBalance(Long id) {
        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id)));
        return BalanceResponse.builder()
                .balance(bankCard.getBalance())
                .build();
    }

    private BankUserResponse getBankUser(Long bankUserId, BankUser bankUser) {
        BankUserResponse bankUserResponse;
        if (bankUser.equals(BankUser.PASSENGER)) {
            bankUserResponse = passengerWebClient.getPassenger(bankUserId);
        } else {
            bankUserResponse = driverWebClient.getDriver(bankUserId);
        }
        return bankUserResponse;
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