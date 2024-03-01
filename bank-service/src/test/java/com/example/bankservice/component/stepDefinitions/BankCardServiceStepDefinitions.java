package com.example.bankservice.component.stepDefinitions;

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
import com.example.bankservice.exception.IncorrectFieldNameException;
import com.example.bankservice.mapper.BankCardMapper;
import com.example.bankservice.model.BankCard;
import com.example.bankservice.model.enums.BankUser;
import com.example.bankservice.repository.BankCardRepository;
import com.example.bankservice.service.impl.BankCardServiceImpl;
import com.example.bankservice.util.FieldValidator;
import com.example.bankservice.util.TestBankCardUtil;
import com.example.bankservice.webClient.PassengerWebClient;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class BankCardServiceStepDefinitions {
    @Mock
    private BankCardRepository bankCardRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FieldValidator fieldValidator;

    @Mock
    private BankCardMapper bankCardMapper;

    @Mock
    private PassengerWebClient passengerWebClient;

    @InjectMocks
    private BankCardServiceImpl bankCardService;

    private BankCardRequest bankCardRequest;
    private UpdateBankCardRequest updateBankCardRequest;
    private WithdrawalRequest withdrawalRequest;
    private RefillRequest refillRequest;
    private BankCard bankCard;
    private BankCardResponse expected;
    private BalanceResponse expectedBalanceResponse;
    private BankCardResponse actual;
    private BalanceResponse actualBalanceResponse;
    private Exception exception;
    private BankCardPageResponse actualPageResponse;
    private BankUserResponse bankUserResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("New bank card has unique number {string}")
    public void newBankCardHasUniqueNumber(String cardNumber) {
        bankCard = TestBankCardUtil.getFirstBankCard();
        bankCard.setNumber(cardNumber);
        bankCardRequest = TestBankCardUtil.getBankCardRequest();
        bankCardRequest.setNumber(cardNumber);
        expected = TestBankCardUtil.getFirstBankCardResponse();

        when(bankCardRepository.findBankCardByNumber(cardNumber))
                .thenReturn(Optional.empty());
        when(bankCardMapper.mapBankCardRequestToBankCard(bankCardRequest))
                .thenReturn(bankCard);
        when(bankCardRepository.save(bankCard))
                .thenReturn(bankCard);

        Optional<BankCard> optionalBankCard = bankCardRepository.findBankCardByNumber(cardNumber);
        assertFalse(optionalBankCard.isPresent());
    }

    @Given("New bank card has existing number {string}")
    public void givenBankCardWithExistingNumber(String cardNumber) {
        bankCard = TestBankCardUtil.getFirstBankCard();
        bankCard.setNumber(cardNumber);
        bankCardRequest = TestBankCardUtil.getBankCardRequest();
        bankCardRequest.setNumber(cardNumber);

        when(bankCardRepository.findBankCardByNumber(cardNumber))
                .thenReturn(Optional.of(bankCard));

        Optional<BankCard> optionalBankCard = bankCardRepository.findBankCardByNumber(cardNumber);
        assertTrue(optionalBankCard.isPresent());
    }

    @When("Method createBankCard called")
    public void whenCreateBankCardMethodCalled() {
        try {
            actual = bankCardService.createBankCard(bankCardRequest);
        } catch (CardNumberUniqueException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the created bank card")
    public void thenResponseShouldContainCreatedBankCardDetails() {
        when(bankCardMapper.mapBankCardToBankCardResponse(eq(bankCard), any()))
                .thenReturn(expected);

        bankCard = bankCardRepository.save(bankCard);
        expected = bankCardMapper.mapBankCardToBankCardResponse(eq(bankCard), any());

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The CardNumberUniqueException should be thrown with message {string}")
    public void thenCardNumberUniqueExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("Editing bank card with id {long} exists and number {string} unique")
    public void editingBankCardWithIdExistsAndNumberUnique(long id, String cardNumber) {
        updateBankCardRequest = TestBankCardUtil.getUpdateBankCardRequest();
        bankCard = TestBankCardUtil.getFirstBankCard();
        expected = TestBankCardUtil.getFirstBankCardResponse();

        when(bankCardRepository.findById(id))
                .thenReturn(Optional.of(bankCard));
        when(bankCardRepository.findBankCardByNumber(cardNumber))
                .thenReturn(Optional.empty());
        when(modelMapper.map(updateBankCardRequest, BankCard.class))
                .thenReturn(bankCard);
        when(bankCardRepository.save(bankCard))
                .thenReturn(bankCard);
        when(modelMapper.map(bankCard, BankCardResponse.class))
                .thenReturn(expected);

        Optional<BankCard> optionalBankCard = bankCardRepository.findBankCardByNumber(cardNumber);
        assertFalse(optionalBankCard.isPresent());
    }

    @Given("Editing bank card with id {long} has existing number {string}")
    public void editingBankCardHasExistingNumber(long id, String cardNumber) {
        bankCard = TestBankCardUtil.getFirstBankCard();
        bankCard.setNumber(cardNumber);
        updateBankCardRequest = TestBankCardUtil.getUpdateBankCardRequest();
        updateBankCardRequest.setNumber(cardNumber);

        when(bankCardRepository.findById(id))
                .thenReturn(Optional.of(bankCard));
        when(bankCardRepository.findBankCardByNumber(cardNumber))
                .thenReturn(Optional.of(bankCard));

        Optional<BankCard> optionalBankCard = bankCardRepository.findBankCardByNumber(cardNumber);
        assertTrue(optionalBankCard.isPresent());
    }

    @Given("There is no bank card with id {long}")
    public void givenBankCardWithIdNotExists(long id) {
        bankCard = TestBankCardUtil.getFirstBankCard();
        bankCard.setId(id);

        when(bankCardRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<BankCard> bankCard = bankCardRepository.findById(id);
        assertFalse(bankCard.isPresent());
    }

    @When("Method editBankCard called with id {long}")
    public void methodEditBankCardCalled(long id) {
        try {
            actual = bankCardService.editBankCard(id, updateBankCardRequest);
        } catch (CardNumberUniqueException | BankCardNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the edited bank card")
    public void theResponseShouldContainTheDetailsOfTheEditedBankCard() {
        bankCard = bankCardRepository.save(bankCard);
        expected = bankCardMapper.mapBankCardToBankCardResponse(eq(bankCard), any());

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The BankCardNotFoundException should be thrown with message {string}")
    public void thenBankCardNotFoundExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("There is a bank card with id {long}")
    public void givenBankCardWithIdExists(long id) {
        bankCard = TestBankCardUtil.getFirstBankCard();
        expected = TestBankCardUtil.getFirstBankCardResponse();

        when(bankCardRepository.findById(id))
                .thenReturn(Optional.of(bankCard));
        when(modelMapper.map(bankCard, BankCardResponse.class))
                .thenReturn(expected);

        assertTrue(bankCardRepository.findById(id).isPresent());
    }

    @When("Method getBankCardById called with id {long}")
    public void whenGetBankCardByIdMethodCalled(long id) {
        try {
            actual = bankCardService.getBankCardById(id);
        } catch (BankCardNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of bank card with id {long}")
    public void thenResponseShouldContainBankCardDetails(long id) {
        bankCard = bankCardRepository.findById(id).get();
        expected = bankCardMapper.mapBankCardToBankCardResponse(eq(bankCard), any());

        assertThat(actual).isEqualTo(expected);
    }

    @Given("Deleting bank card with id {long} exists")
    public void deletingBankCardWithIdExists(long id) {
        bankCard = TestBankCardUtil.getFirstBankCard();

        when(bankCardRepository.findById(id))
                .thenReturn(Optional.of(bankCard));

        assertTrue(bankCardRepository.findById(id).isPresent());
    }

    @When("Method deleteBankCard called with id {long}")
    public void methodDeleteBankCardCalledWithId(long id) {
        try {
            bankCardService.deleteBankCard(id);
        } catch (BankCardNotFoundException ex) {
            exception = ex;
        }
    }

    @Given("There are bank cards of user {string} with id {string} in the system in page {int} with size {int} and sort by {string}")
    public void thereAreBankCardsOfUserWithIdInTheSystem(String bankUser, String bankUserId, int page, int size, String sortBy) {
        List<BankCard> bankCards = TestBankCardUtil.getBankCards();
        BankCard firstBankCard = bankCards.get(0);
        List<BankCardResponse> bankCardResponses = TestBankCardUtil.getBankCardResponses();
        bankUserResponse = TestBankCardUtil.getBankUserResponse();
        bankUserResponse.setId(bankUserId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankCard> mockBankCardPage = new PageImpl<>(bankCards, pageable, bankCards.size());

        doNothing()
                .when(fieldValidator)
                .checkSortField(any(), eq(sortBy));
        when(bankCardRepository.findAllByBankUserIdAndBankUser(bankUserId, BankUser.valueOf(bankUser), pageable))
                .thenReturn(mockBankCardPage);
        when(passengerWebClient.getPassenger(firstBankCard.getBankUserId()))
                .thenReturn(bankUserResponse);
        when(bankCardMapper.mapBankCardToBankCardResponse(bankCards.get(0), bankUserResponse))
                .thenReturn(bankCardResponses.get(0));
        when(bankCardMapper.mapBankCardToBankCardResponse(bankCards.get(1), bankUserResponse))
                .thenReturn(bankCardResponses.get(1));

        Page<BankCard> bankCardPage =
                bankCardRepository.findAllByBankUserIdAndBankUser(bankUserId, BankUser.valueOf(bankUser), pageable);
        assertTrue(bankCardPage.hasContent());
    }

    @When("Method getBankCardsByBankUser for user {string} with id {string} called with page {int}, size {int}, and sort by {string}")
    public void methodGetBankCardsByBankUserCalledWithPageSizeAndSortBy(String bankUser, String bankUserId, int page, int size, String sortBy) {
        try {
            actualPageResponse = bankCardService.getBankCardsByBankUser(bankUserId, BankUser.valueOf(bankUser), page, size, sortBy);
        } catch (IncorrectFieldNameException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain a page of user bank cards number {int} with size {int}")
    public void theResponseShouldContainAListOfUserBankCards(int page, int size) {
        List<BankCardResponse> bankCardResponses = TestBankCardUtil.getBankCardResponses();

        BankCardPageResponse expectedPageResponse = BankCardPageResponse.builder()
                .bankCards(bankCardResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        assertEquals(expectedPageResponse, actualPageResponse);
    }

    @Given("There is a default user {string} with id {string} bank card")
    public void thereIsADefaultUserWithIdBankCard(String bankUser, String bankUserId) {
        bankCard = TestBankCardUtil.getFirstBankCard();
        bankUserResponse = TestBankCardUtil.getBankUserResponse();
        expected = TestBankCardUtil.getFirstBankCardResponse();

        when(passengerWebClient.getPassenger(bankUserId))
                .thenReturn(bankUserResponse);
        when(bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, BankUser.valueOf(bankUser)))
                .thenReturn(Optional.of(bankCard));
        when(bankCardMapper.mapBankCardToBankCardResponse(bankCard, bankUserResponse))
                .thenReturn(expected);
    }

    @Given("There is no default user {string} with id {string} bank card")
    public void thereIsNoDefaultUserWithIdBankCard(String bankUser, String bankUserId) {
        when(bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, BankUser.valueOf(bankUser)))
                .thenReturn(Optional.empty());

        assertTrue(bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, BankUser.valueOf(bankUser))
                .isEmpty());
    }

    @When("Method getDefaultBankCard of user {string} with id {string} called")
    public void methodGetDefaultBankCardOfUserWithIdCalled(String bankUser, String bankUserId) {
        try {
            actual = bankCardService.getDefaultBankCard(bankUserId, BankUser.valueOf(bankUser));
        } catch (BankCardNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of default user bank card")
    public void theResponseShouldContainTheDetailsOfDefaultUserBankCard() {
        assertEquals(expected, actual);
    }

    @Given("There is a bank card with id {long} to retrieval balance")
    public void thereIsABankCardWithIdToRetrievalBalance(long id) {
        bankCard = TestBankCardUtil.getFirstBankCard();
        expectedBalanceResponse = TestBankCardUtil.getBalanceResponse();

        when(bankCardRepository.findById(id))
                .thenReturn(Optional.of(bankCard));
    }

    @When("Method getBankCardBalance called with id {long}")
    public void methodGetBankCardBalanceCalledWithId(long id) {
        try {
            actualBalanceResponse = bankCardService.getBankCardBalance(id);
        } catch (BankCardNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of bank card balance")
    public void theResponseShouldContainTheDetailsOfBankCardBalanceWithId() {
        assertThat(actualBalanceResponse).isEqualTo(expectedBalanceResponse);
    }

    @Given("There is bank card with id {long} with sufficient balance")
    public void thereIsBankCardWithIdWithSufficientBalance(long id) {
        withdrawalRequest = TestBankCardUtil.getWithdrawalRequest();
        bankCard = TestBankCardUtil.getFirstBankCard();
        expected = TestBankCardUtil.getFirstBankCardResponse();
        expected.setBalance(bankCard.getBalance().subtract(withdrawalRequest.getSum()));

        when(bankCardRepository.findById(id))
                .thenReturn(Optional.of(bankCard));
        when(bankCardRepository.save(bankCard))
                .thenReturn(bankCard);
        when(passengerWebClient.getPassenger(expected.getBankUser().getId()))
                .thenReturn(expected.getBankUser());
        when(bankCardMapper.mapBankCardToBankCardResponse(bankCard, expected.getBankUser()))
                .thenReturn(expected);

        assertTrue(bankCardRepository.findById(id).isPresent());
    }

    @Given("There is bank card with id {long} with insufficient balance to pay {int} BYN")
    public void thereIsBankCardWithIdWithInsufficientBalance(long id, int sum) {
        withdrawalRequest = TestBankCardUtil.getWithdrawalRequest();
        withdrawalRequest.setSum(BigDecimal.valueOf(sum));
        BankCard bankCard = TestBankCardUtil.getFirstBankCard();
        bankCard.setBalance(BigDecimal.valueOf(30));

        when(bankCardRepository.findById(id))
                .thenReturn(Optional.of(bankCard));
    }

    @When("Method withdrawalPaymentFromBankCard for card with id {long} called")
    public void methodWithdrawalPaymentFromBankCardForCardWithIdCalled(long id) {
        try {
            actual = bankCardService.withdrawalPaymentFromBankCard(id, withdrawalRequest);
        } catch (BankCardNotFoundException | BankCardBalanceException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of bank card with id {long} after withdrawal payment")
    public void theResponseShouldContainTheDetailsOfBankCardWithIdAfterWithdrawalPayment(long id) {
        assertEquals(expected, actual);
    }

    @Then("The BankCardBalanceException should be thrown with message {string};")
    public void theBankCardBalanceExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("There is bank card with id {long} to refill")
    public void thereIsBankCardWithIdToRefill(long id) {
        String bankUserId = TestBankCardUtil.getBankUserId();
        refillRequest = TestBankCardUtil.getRefillRequest();
        bankCard = TestBankCardUtil.getFirstBankCard();
        bankUserResponse = TestBankCardUtil.getBankUserResponse();
        expected = TestBankCardUtil.getFirstBankCardResponse();

        when(passengerWebClient.getPassenger(bankUserId))
                .thenReturn(bankUserResponse);
        when(bankCardRepository.findById(id))
                .thenReturn(Optional.of(bankCard));
        when(bankCardRepository.save(bankCard))
                .thenReturn(bankCard);
        when(bankCardMapper.mapBankCardToBankCardResponse(bankCard, bankUserResponse))
                .thenReturn(expected);

        Optional<BankCard> bankCard = bankCardRepository.findById(id);
        assertTrue(bankCard.isPresent());
    }


    @Given("Bank card id to refill not provided")
    public void bankCardIdNotProvided() {
        String bankUserId = TestBankCardUtil.getBankUserId();
        refillRequest = TestBankCardUtil.getRefillRequest();
        bankCard = TestBankCardUtil.getFirstBankCard();
        bankUserResponse = TestBankCardUtil.getBankUserResponse();
        expected = TestBankCardUtil.getFirstBankCardResponse();
        BankUser bankUser = BankUser.DRIVER;

        when(passengerWebClient.getPassenger(bankUserId))
                .thenReturn(bankUserResponse);
        when(bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, bankUser))
                .thenReturn(Optional.of(bankCard));
        when(bankCardRepository.save(bankCard))
                .thenReturn(bankCard);
        when(bankCardMapper.mapBankCardToBankCardResponse(bankCard, bankUserResponse))
                .thenReturn(expected);

        Optional<BankCard> bankCard = bankCardRepository.findByBankUserIdAndBankUserAndIsDefaultTrue(bankUserId, bankUser);
        assertTrue(bankCard.isPresent());
    }

    @When("Method refillBankCard for card with id {long} called")
    public void methodRefillBankCardForCardWithIdCalled(long id) {
        try {
            actual = bankCardService.refillBankCard(id, refillRequest);
        } catch (BankCardNotFoundException ex) {
            exception = ex;
        }
    }

    @When("Method refillBankCard for card without called")
    public void methodRefillBankCardForCardWithoutCalled() {
        try {
            actual = bankCardService.refillBankCard(null, refillRequest);
        } catch (BankCardNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of bank card with id {long} after refilling")
    public void theResponseShouldContainTheDetailsOfBankCardWithIdAfterRefilling(long id) {
        assertEquals(expected, actual);
    }
}
