package com.example.bankservice.junit;

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
import com.example.bankservice.service.impl.BankCardServiceImpl;
import com.example.bankservice.util.FieldValidator;
import com.example.bankservice.util.TestBankCardUtil;
import com.example.bankservice.webClient.PassengerWebClient;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankCardServiceTest {
    @Mock
    BankCardRepository bankCardRepository;
    @Mock
    BankCardMapper bankCardMapper;
    @Mock
    PassengerWebClient passengerWebClient;
    @Mock
    FieldValidator fieldValidator;
    @InjectMocks
    BankCardServiceImpl bankCardService;

    @Test
    void testCreateBankCard_WhenCardNumberUnique_ShouldCreateBankCard() {
        BankCardRequest bankCardRequest = TestBankCardUtil.getBankCardRequest();
        BankCard newBankCard = TestBankCardUtil.getFirstBankCard();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        when(bankCardMapper.mapBankCardRequestToBankCard(bankCardRequest))
                .thenReturn(newBankCard);
        when(bankCardRepository.findBankCardByNumber(anyString()))
                .thenReturn(Optional.empty());
        when(bankCardRepository.save(newBankCard))
                .thenReturn(newBankCard);
        when(bankCardMapper.mapBankCardToBankCardResponse(eq(newBankCard), any()))
                .thenReturn(expected);

        BankCardResponse actual = bankCardService.createBankCard(bankCardRequest);

        assertEquals(expected, actual);

        verify(bankCardRepository, times(1))
                .findBankCardByNumber(anyString());
        verify(bankCardRepository, times(1))
                .save(newBankCard);
    }

    @Test
    void testCreateBankCard_WhenCardNumberAlreadyExists_ShouldThrownCardNumberUniqueException() {
        BankCardRequest bankCardRequest = TestBankCardUtil.getBankCardRequest();
        String existingCardNumber = TestBankCardUtil.getFirstBankCard().getNumber();
        BankCard existingBankCard = TestBankCardUtil.getFirstBankCard();

        when(bankCardRepository.findBankCardByNumber(existingCardNumber))
                .thenReturn(Optional.of(existingBankCard));

        assertThrows(CardNumberUniqueException.class, () -> bankCardService.createBankCard(bankCardRequest));

        verify(bankCardRepository, times(1))
                .findBankCardByNumber(existingCardNumber);
    }

    @Test
    void testEditBankCard_WhenCardExistsAndIsValid_ShouldEditBankCard() {
        Long cardId = TestBankCardUtil.getBankCardId();
        UpdateBankCardRequest updateRequest = TestBankCardUtil.getUpdateBankCardRequest();
        BankCard updatedBankCard = TestBankCardUtil.getFirstBankCard();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        when(bankCardRepository.findById(cardId))
                .thenReturn(Optional.of(updatedBankCard));
        when(bankCardRepository.findBankCardByNumber(updateRequest.getNumber()))
                .thenReturn(Optional.empty());
        when(bankCardRepository.save(updatedBankCard))
                .thenReturn(updatedBankCard);
        when(passengerWebClient.getPassenger(updatedBankCard.getBankUserId()))
                .thenReturn(expected.getBankUser());
        when(bankCardMapper.mapBankCardToBankCardResponse(updatedBankCard, expected.getBankUser()))
                .thenReturn(expected);

        BankCardResponse actual = bankCardService.editBankCard(cardId, updateRequest);

        assertEquals(expected, actual);

        verify(bankCardRepository, times(1))
                .findById(cardId);
        verify(bankCardRepository, times(1))
                .findBankCardByNumber(updateRequest.getNumber());
        verify(bankCardRepository, times(1))
                .save(updatedBankCard);
        verify(passengerWebClient, times(1))
                .getPassenger(updatedBankCard.getBankUserId());
    }

    @Test
    void testEditBankCard_WhenCardNotFound_ShouldEditBankCard() {
        Long cardId = TestBankCardUtil.getBankCardId();
        UpdateBankCardRequest updateRequest = TestBankCardUtil.getUpdateBankCardRequest();

        when(bankCardRepository.findById(cardId))
                .thenReturn(Optional.empty());

        assertThrows(BankCardNotFoundException.class, () -> bankCardService.editBankCard(cardId, updateRequest));

        verify(bankCardRepository, times(1))
                .findById(cardId);
        verify(bankCardRepository, never())
                .findBankCardByNumber(any());
        verify(bankCardRepository, never())
                .save(any());
    }

    @Test
    void testEditBankCard_WhenCardNumberAlreadyExists_ShouldEditBankCard() {
        Long cardId = TestBankCardUtil.getBankCardId();
        UpdateBankCardRequest updateRequest = TestBankCardUtil.getUpdateBankCardRequest();
        BankCard existingBankCard = TestBankCardUtil.getFirstBankCard();

        when(bankCardRepository.findById(cardId)).thenReturn(Optional.of(existingBankCard));
        when(bankCardRepository.findBankCardByNumber(updateRequest.getNumber()))
                .thenReturn(Optional.of(TestBankCardUtil.getSecondBankCard()));

        assertThrows(CardNumberUniqueException.class, () -> bankCardService.editBankCard(cardId, updateRequest));

        verify(bankCardRepository, times(1))
                .findById(cardId);
        verify(bankCardRepository, times(1))
                .findBankCardByNumber(updateRequest.getNumber());
        verify(bankCardRepository, never())
                .save(any());
    }

    @Test
    void testDeleteBankCard_WhenBankCardExists_ShouldDeleteBankCard() {
        Long cardId = TestBankCardUtil.getBankCardId();
        BankCard bankCard = TestBankCardUtil.getFirstBankCard();

        when(bankCardRepository.findById(cardId))
                .thenReturn(Optional.of(bankCard));

        bankCardService.deleteBankCard(cardId);

        verify(bankCardRepository, times(1))
                .findById(cardId);
        verify(bankCardRepository, times(1))
                .delete(bankCard);
    }

    @Test
    void testDeleteBankCard_WhenBankCardNotExists_ShouldThrowBankCardNotFoundException() {
        Long cardId = TestBankCardUtil.getBankCardId();

        when(bankCardRepository.findById(cardId))
                .thenReturn(Optional.empty());

        assertThrows(BankCardNotFoundException.class, () -> bankCardService.deleteBankCard(cardId));

        verify(bankCardRepository, times(1))
                .findById(cardId);
        verify(bankCardRepository, never())
                .delete(any());
    }

    @Test
    void testDeleteBankUserCards_ShouldDeleteBankUserCards() {
        String bankUserId = TestBankCardUtil.getBankUserId();
        BankUser bankUser = BankUser.PASSENGER;

        bankCardService.deleteBankUserCards(bankUserId, bankUser);

        verify(bankCardRepository, times(1))
                .deleteAllByBankUserIdAndBankUser(bankUserId, bankUser);
    }

    @Test
    void testWithdrawalPaymentFromBankCard_WithSufficientBalance_ShouldWithdraw() {
        Long cardId = TestBankCardUtil.getBankCardId();
        WithdrawalRequest withdrawalRequest = TestBankCardUtil.getWithdrawalRequest();
        BankCard bankCard = TestBankCardUtil.getFirstBankCard();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();
        expected.setBalance(bankCard.getBalance().subtract(withdrawalRequest.getSum()));

        when(bankCardRepository.findById(cardId))
                .thenReturn(Optional.of(bankCard));
        when(bankCardRepository.save(bankCard))
                .thenReturn(bankCard);
        when(passengerWebClient.getPassenger(expected.getBankUser().getId()))
                .thenReturn(expected.getBankUser());
        when(bankCardMapper.mapBankCardToBankCardResponse(bankCard, expected.getBankUser()))
                .thenReturn(expected);

        BankCardResponse actual = bankCardService.withdrawalPaymentFromBankCard(cardId, withdrawalRequest);

        assertEquals(expected, actual);

        verify(bankCardRepository, times(1))
                .findById(cardId);
        verify(bankCardRepository, times(1))
                .save(bankCard);
    }

    @Test
    void testWithdrawalPaymentFromBankCard_WithInsufficientBalance_ShouldThrowException() {
        Long cardId = TestBankCardUtil.getBankCardId();
        WithdrawalRequest withdrawalRequest = TestBankCardUtil.getWithdrawalRequest();
        BankCard bankCard = TestBankCardUtil.getFirstBankCard();
        bankCard.setBalance(BigDecimal.valueOf(50));

        when(bankCardRepository.findById(cardId))
                .thenReturn(Optional.of(bankCard));

        assertThrows(BankCardBalanceException.class, () -> bankCardService.withdrawalPaymentFromBankCard(cardId, withdrawalRequest));

        verify(bankCardRepository, times(1))
                .findById(cardId);
        verify(bankCardRepository, never())
                .save(any());
    }

    @Test
    void testGetBankCardById_WhenCardExists_ShouldReturnBankCardResponse() {
        Long cardId = TestBankCardUtil.getBankCardId();
        BankCard bankCard = TestBankCardUtil.getFirstBankCard();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        when(bankCardRepository.findById(cardId))
                .thenReturn(Optional.of(bankCard));
        when(bankCardMapper.mapBankCardToBankCardResponse(any(), any()))
                .thenReturn(expected);

        BankCardResponse actual = bankCardService.getBankCardById(cardId);

        assertEquals(expected, actual);

        verify(bankCardRepository, times(1))
                .findById(cardId);
    }

    @Test
    void testGetBankCardById_WhenCardDoesNotExist_ShouldThrowNotFoundException() {
        Long cardId = TestBankCardUtil.getBankCardId();

        when(bankCardRepository.findById(cardId))
                .thenReturn(Optional.empty());

        assertThrows(BankCardNotFoundException.class, () -> bankCardService.getBankCardById(cardId));

        verify(bankCardRepository, times(1))
                .findById(cardId);
    }

    @Test
    void testGetBankCardsByBankUser_ShouldReturnBankCardPageResponse() {
        int page = TestBankCardUtil.getPageNumber();
        int size = TestBankCardUtil.getPageSize();
        String sortBy = TestBankCardUtil.getCorrectSortField();
        List<BankCard> bankCards = TestBankCardUtil.getBankCards();
        BankCard firstBankCard = bankCards.get(0);
        List<BankCardResponse> bankCardResponses = TestBankCardUtil.getBankCardResponses();
        BankUserResponse bankUserResponse = TestBankCardUtil.getBankUserResponse();

        Pageable pageable = PageRequest.of(page, size);
        Page<BankCard> bankCardPage = new PageImpl<>(bankCards, pageable, bankCards.size());

        BankCardPageResponse expected = BankCardPageResponse.builder()
                .bankCards(bankCardResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        doNothing()
                .when(fieldValidator)
                .checkSortField(any(), eq(sortBy));
        when(bankCardRepository.findAllByBankUserIdAndBankUser(eq(firstBankCard.getBankUserId()),
                eq(firstBankCard.getBankUser()), any(Pageable.class)))
                .thenReturn(bankCardPage);
        when(passengerWebClient.getPassenger(firstBankCard.getBankUserId()))
                .thenReturn(bankUserResponse);
        when(bankCardMapper.mapBankCardToBankCardResponse(firstBankCard, bankUserResponse))
                .thenReturn(bankCardResponses.get(0));
        when(bankCardMapper.mapBankCardToBankCardResponse(bankCards.get(1), bankUserResponse))
                .thenReturn(bankCardResponses.get(1));

        BankCardPageResponse actual = bankCardService.getBankCardsByBankUser(firstBankCard.getBankUserId(),
                firstBankCard.getBankUser(), page, size, sortBy);

        assertEquals(expected, actual);

        verify(bankCardRepository, times(1))
                .findAllByBankUserIdAndBankUser(eq(firstBankCard.getBankUserId()),
                        eq(firstBankCard.getBankUser()), any(Pageable.class));
        verify(passengerWebClient, times(2))
                .getPassenger(firstBankCard.getBankUserId());
    }

    @Test
    void testGetDefaultBankCard_WhenBankCardExists_ShouldReturnDefaultBankCardResponse() {
        BankCard defaultBankCard = TestBankCardUtil.getFirstBankCard();
        BankUserResponse bankUserResponse = TestBankCardUtil.getBankUserResponse();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();
        String bankUserId = defaultBankCard.getBankUserId();
        BankUser bankUser = defaultBankCard.getBankUser();

        when(passengerWebClient.getPassenger(bankUserId))
                .thenReturn(bankUserResponse);
        when(bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, bankUser))
                .thenReturn(Optional.of(defaultBankCard));
        when(bankCardMapper.mapBankCardToBankCardResponse(defaultBankCard, bankUserResponse))
                .thenReturn(expected);

        BankCardResponse actual = bankCardService.getDefaultBankCard(bankUserId, bankUser);

        assertEquals(expected, actual);

        verify(bankCardRepository, times(1))
                .findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, bankUser);
    }

    @Test
    void testGetDefaultBankCard_WhenNoDefaultBankCard_ShouldThrowException() {
        String bankUserId = TestBankCardUtil.getBankUserId();
        BankUser bankUser = TestBankCardUtil.getFirstBankCard().getBankUser();

        when(bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, bankUser))
                .thenReturn(Optional.empty());

        assertThrows(BankCardNotFoundException.class, () -> bankCardService.getDefaultBankCard(bankUserId, bankUser));

        verify(bankCardRepository, times(1))
                .findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, bankUser);
    }


    @Test
    void testGetBankCardBalance_WhenBankCardExists_ShouldReturnBalanceResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        BankCard bankCard = TestBankCardUtil.getFirstBankCard();
        BalanceResponse expected = TestBankCardUtil.getBalanceResponse();

        when(bankCardRepository.findById(bankCardId))
                .thenReturn(Optional.of(bankCard));

        BalanceResponse actual = bankCardService.getBankCardBalance(bankCardId);

        assertEquals(expected, actual);

        verify(bankCardRepository, times(1))
                .findById(bankCardId);
    }

    @Test
    void testRefillBankCard_WithIdProvided_ShouldRefillBankCard() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        String bankUserId = TestBankCardUtil.getBankUserId();
        RefillRequest refillRequest = TestBankCardUtil.getRefillRequest();
        BankCard bankCard = TestBankCardUtil.getFirstBankCard();
        BankUserResponse bankUserResponse = TestBankCardUtil.getBankUserResponse();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        when(passengerWebClient.getPassenger(bankUserId))
                .thenReturn(bankUserResponse);
        when(bankCardRepository.findById(bankCardId))
                .thenReturn(Optional.of(bankCard));
        when(bankCardRepository.save(bankCard))
                .thenReturn(bankCard);
        when(bankCardMapper.mapBankCardToBankCardResponse(bankCard, bankUserResponse))
                .thenReturn(expected);

        BankCardResponse actual = bankCardService.refillBankCard(bankCardId, refillRequest);

        assertEquals(expected, actual);

        verify(passengerWebClient, times(1))
                .getPassenger(bankUserId);
        verify(bankCardRepository, times(1))
                .save(bankCard);
    }

    @Test
    void testRefillBankCard_WithoutIdProvided_ShouldRefillDefaultBankCard() {
        String bankUserId = TestBankCardUtil.getBankUserId();
        RefillRequest refillRequest = TestBankCardUtil.getRefillRequest();
        BankCard defaultBankCard = TestBankCardUtil.getFirstBankCard();
        BankUserResponse bankUserResponse = TestBankCardUtil.getBankUserResponse();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();
        BankUser bankUser = BankUser.DRIVER;

        when(passengerWebClient.getPassenger(bankUserId))
                .thenReturn(bankUserResponse);
        when(bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, bankUser))
                .thenReturn(Optional.of(defaultBankCard));
        when(bankCardRepository.save(defaultBankCard))
                .thenReturn(defaultBankCard);
        when(bankCardMapper.mapBankCardToBankCardResponse(defaultBankCard, bankUserResponse))
                .thenReturn(expected);

        BankCardResponse actual = bankCardService.refillBankCard(null, refillRequest);

        assertEquals(expected, actual);

        verify(passengerWebClient, times(1))
                .getPassenger(bankUserId);
        verify(bankCardRepository, times(1))
                .findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, bankUser);
        verify(bankCardRepository, times(1))
                .save(defaultBankCard);
    }

    @Test
    void testRefillBankCard_WithInvalidId_ShouldThrowBankCardNotFoundException() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        RefillRequest refillRequest = TestBankCardUtil.getRefillRequest();

        when(bankCardRepository.findById(bankCardId))
                .thenReturn(Optional.empty());

        assertThrows(BankCardNotFoundException.class, () -> bankCardService.refillBankCard(bankCardId, refillRequest));
    }

    @Test
    void testGetBankCardBalance_WhenBankCardNotFound_ShouldThrowBankCardNotFoundException() {
        Long bankCardId = TestBankCardUtil.getBankCardId();

        when(bankCardRepository.findById(bankCardId))
                .thenReturn(Optional.empty());

        assertThrows(BankCardNotFoundException.class, () -> bankCardService.getBankCardBalance(bankCardId));

        verify(bankCardRepository, times(1))
                .findById(bankCardId);
    }
}