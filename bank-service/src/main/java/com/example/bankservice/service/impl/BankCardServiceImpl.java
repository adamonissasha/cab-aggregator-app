package com.example.bankservice.service.impl;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.exception.BankCardBalanceException;
import com.example.bankservice.exception.BankCardNotFoundException;
import com.example.bankservice.exception.CardNumberUniqueException;
import com.example.bankservice.mapper.BankCardMapper;
import com.example.bankservice.model.BankCard;
import com.example.bankservice.model.enums.BankUser;
import com.example.bankservice.repository.BankCardRepository;
import com.example.bankservice.service.BankCardService;
import com.example.bankservice.util.FieldValidator;
import com.example.bankservice.webClient.DriverWebClient;
import com.example.bankservice.webClient.PassengerWebClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankCardServiceImpl implements BankCardService {
    private static final String CARD_NUMBER_EXIST = "Card with number '%s' already exist";
    private static final String CARD_NOT_FOUND = "Card with id '%s' not found";
    private static final String DEFAULT_CARD_NOT_FOUND = "%s's with id %s default card not found";
    private static final String INSUFFICIENT_CARD_BALANCE_TO_PAY = "There is not enough balance money to pay %s BYN " +
            "for the ride. Refill card or change payment method";
    private final BankCardRepository bankCardRepository;
    private final BankCardMapper bankCardMapper;
    private final PassengerWebClient passengerWebClient;
    private final DriverWebClient driverWebClient;
    private final FieldValidator fieldValidator;

    @Override
    @Transactional
    public BankCardResponse createBankCard(BankCardRequest bankCardRequest) {
        log.info("Creating bank card: {}", bankCardRequest);

        String cardNumber = bankCardRequest.getNumber();
        bankCardRepository.findBankCardByNumber(cardNumber)
                .ifPresent(bankCard -> {
                    log.error("Card with number {} already exist", cardNumber);
                    throw new CardNumberUniqueException(String.format(CARD_NUMBER_EXIST, cardNumber));
                });

        BankCard newBankCard = bankCardMapper.mapBankCardRequestToBankCard(bankCardRequest);
        BankUserResponse bankUserResponse = getBankUser(newBankCard.getBankUserId(), newBankCard.getBankUser());
        newBankCard = bankCardRepository.save(newBankCard);
        return bankCardMapper.mapBankCardToBankCardResponse(newBankCard, bankUserResponse);
    }

    @Override
    @Transactional
    public BankCardResponse editBankCard(Long id, UpdateBankCardRequest updateBankCardRequest) {
        log.info("Updating bank card with id {}: {}", id, updateBankCardRequest);

        BankCard updatedBankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Card with id {} not found", id);
                    return new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id));
                });

        String cardNumber = updateBankCardRequest.getNumber();
        bankCardRepository.findBankCardByNumber(cardNumber)
                .ifPresent(bankCard -> {
                    if (!bankCard.getId().equals(id)) {
                        log.error("Card with number {} already exist", cardNumber);
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
        log.info("Deleting bank card with id: {}", id);

        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Card with id {} not found", id);
                    return new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id));
                });

        bankCardRepository.delete(bankCard);
    }

    @Override
    @Transactional
    public void deleteBankUserCards(String bankUserId, BankUser bankUser) {
        log.info("Deleting bank cards of user {} with id: {}", bankUser, bankUserId);

        bankCardRepository.deleteAllByBankUserIdAndBankUser(bankUserId, bankUser);
    }

    @Override
    @Transactional
    public BankCardResponse withdrawalPaymentFromBankCard(Long id, WithdrawalRequest withdrawalRequest) {
        log.info("Withdrawing payment from bank card with id {}: {}", id, withdrawalRequest);

        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Card with id {} not found", id);
                    return new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id));
                });

        BigDecimal withdrawalSum = withdrawalRequest.getSum();
        BigDecimal bankCardBalance = bankCard.getBalance();
        if (bankCardBalance.compareTo(withdrawalSum) < 0) {
            log.error("There is not enough balance money to pay {} BYN " +
                    "for the ride. Refill card or change payment method", withdrawalSum);
            throw new BankCardBalanceException(String.format(INSUFFICIENT_CARD_BALANCE_TO_PAY, withdrawalSum));
        }

        bankCard.setBalance(bankCardBalance.subtract(withdrawalSum));
        bankCard = bankCardRepository.save(bankCard);
        return bankCardMapper.mapBankCardToBankCardResponse(bankCard,
                getBankUser(bankCard.getBankUserId(), bankCard.getBankUser()));
    }

    @Override
    @Transactional
    public BankCardResponse getBankCardById(Long id) {
        log.info("Retrieving bank card with id: {}", id);

        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Card with id {} not found", id);
                    return new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id));
                });

        return bankCardMapper.mapBankCardToBankCardResponse(bankCard,
                getBankUser(bankCard.getBankUserId(), bankCard.getBankUser()));
    }

    @Override
    @Transactional
    public BankCardPageResponse getBankCardsByBankUser(String bankUserId, BankUser bankUser, int page, int size, String sortBy) {
        log.info("Retrieving bank cards of user {} with id {} (page {}, size {}, sorted by {})", bankUser, bankUserId, page, size, sortBy);

        fieldValidator.checkSortField(BankCard.class, sortBy);

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
    @Transactional
    public BankCardResponse makeBankCardDefault(Long id) {
        log.info("Making bank card default by id: {}", id);

        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Card with id {} not found", id);
                    return new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id));
                });

        bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankCard.getBankUserId(), bankCard.getBankUser())
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
    @Transactional
    public BankCardResponse getDefaultBankCard(String bankUserId, BankUser bankUser) {
        log.info("Retrieving default bank card of user {} with id : {}", bankUser, bankUserId);

        BankCard defaultBankCard = bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, bankUser)
                .orElseThrow(() -> {
                    log.error("{}'s with id {} default card not found", bankUser, bankUserId);
                    return new BankCardNotFoundException(String.format(DEFAULT_CARD_NOT_FOUND, bankUser, bankUserId));
                });

        BankUserResponse bankUserResponse = getBankUser(bankUserId, bankUser);
        return bankCardMapper.mapBankCardToBankCardResponse(defaultBankCard, bankUserResponse);
    }

    @Override
    @Transactional
    public BankCardResponse refillBankCard(Long id, RefillRequest refillRequest) {
        log.info("Refilling bank card with id {}: {}", id, refillRequest);

        BankCard bankCard;
        if (id == null) {
            BankUser bankUser = BankUser.DRIVER;
            String bankUserId = refillRequest.getBankUserId();
            bankCard = bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, bankUser)
                    .orElseThrow(() -> {
                        log.error("{}'s with id {} default card not found", bankUser, bankUserId);
                        return new BankCardNotFoundException(String.format(DEFAULT_CARD_NOT_FOUND, bankUser, bankUserId));
                    });
        } else {
            bankCard = bankCardRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Card with id {} not found", id);
                        return new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id));
                    });
        }

        bankCard.setBalance(bankCard.getBalance().add(refillRequest.getSum()));
        bankCard = bankCardRepository.save(bankCard);
        return bankCardMapper.mapBankCardToBankCardResponse(bankCard,
                getBankUser(bankCard.getBankUserId(), bankCard.getBankUser()));
    }

    @Override
    public BalanceResponse getBankCardBalance(Long id) {
        log.info("Retrieving balance for bank card with id {}", id);

        BankCard bankCard = bankCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Card with id {} not found", id);
                    return new BankCardNotFoundException(String.format(CARD_NOT_FOUND, id));
                });

        return BalanceResponse.builder()
                .balance(bankCard.getBalance())
                .build();
    }

    private BankUserResponse getBankUser(String bankUserId, BankUser bankUser) {
        BankUserResponse bankUserResponse;

        if (bankUser == BankUser.PASSENGER) {
            bankUserResponse = passengerWebClient.getPassenger(bankUserId);
        } else {
            bankUserResponse = driverWebClient.getDriver(Long.parseLong(bankUserId));
        }

        return bankUserResponse;
    }
}