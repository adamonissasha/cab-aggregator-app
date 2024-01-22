package com.example.bankservice.component.stepDefinitions;

import com.example.bankservice.dto.request.BankAccountRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankAccountPageResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.exception.AccountNumberUniqueException;
import com.example.bankservice.exception.BankAccountNotFoundException;
import com.example.bankservice.exception.DriverBankAccountException;
import com.example.bankservice.exception.IncorrectFieldNameException;
import com.example.bankservice.mapper.BankAccountMapper;
import com.example.bankservice.model.BankAccount;
import com.example.bankservice.model.enums.BankUser;
import com.example.bankservice.repository.BankAccountRepository;
import com.example.bankservice.service.BankAccountHistoryService;
import com.example.bankservice.service.BankCardService;
import com.example.bankservice.service.impl.BankAccountServiceImpl;
import com.example.bankservice.util.FieldValidator;
import com.example.bankservice.util.TestBankAccountUtil;
import com.example.bankservice.util.TestBankCardUtil;
import com.example.bankservice.webClient.DriverWebClient;
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

public class BankAccountServiceStepDefinitions {
    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FieldValidator fieldValidator;

    @Mock
    private BankAccountMapper bankAccountMapper;

    @Mock
    private DriverWebClient driverWebClient;

    @Mock
    private BankAccountHistoryService bankAccountHistoryService;

    @Mock
    private BankCardService bankCardService;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private BankAccountRequest bankAccountRequest;
    private WithdrawalRequest withdrawalRequest;
    private RefillRequest refillRequest;
    private BankAccount bankAccount;
    private BankAccountResponse expected;
    private BalanceResponse expectedBalanceResponse;
    private BankAccountResponse actual;
    private BalanceResponse actualBalanceResponse;
    private Exception exception;
    private BankAccountPageResponse actualPageResponse;
    private BankUserResponse bankUserResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("New bank account has unique number {string}")
    public void newBankAccountHasUniqueNumber(String accountNumber) {
        bankAccount = TestBankAccountUtil.getFirstBankAccount();
        bankAccount.setNumber(accountNumber);
        bankAccountRequest = TestBankAccountUtil.getBankAccountRequest();
        bankAccountRequest.setNumber(accountNumber);
        expected = TestBankAccountUtil.getFirstBankAccountResponse();

        when(bankAccountRepository.findByNumber(accountNumber))
                .thenReturn(Optional.empty());
        when(bankAccountMapper.mapBankAccountRequestToBankAccount(bankAccountRequest))
                .thenReturn(bankAccount);
        when(bankAccountRepository.save(bankAccount))
                .thenReturn(bankAccount);

        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findByNumber(accountNumber);
        assertFalse(optionalBankAccount.isPresent());
    }

    @Given("New bank account has existing number {string}")
    public void givenBankAccountWithExistingNumber(String accountNumber) {
        bankAccount = TestBankAccountUtil.getFirstBankAccount();
        bankAccount.setNumber(accountNumber);
        bankAccountRequest = TestBankAccountUtil.getBankAccountRequest();
        bankAccountRequest.setNumber(accountNumber);

        when(bankAccountRepository.findByNumber(accountNumber))
                .thenReturn(Optional.of(bankAccount));

        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findByNumber(accountNumber);
        assertTrue(optionalBankAccount.isPresent());
    }

    @Given("New bank account created for driver with id {long} who already has account")
    public void newBankAccountCreatedForDriverWithIdWhoAlreadyHasAccount(long id) {
        bankAccountRequest = TestBankAccountUtil.getBankAccountRequest();
        bankAccount = TestBankAccountUtil.getFirstBankAccount();

        when(bankAccountRepository.findByDriverId(id))
                .thenReturn(Optional.of(bankAccount));

        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findByDriverId(id);
        assertTrue(optionalBankAccount.isPresent());
    }

    @When("Method createBankAccount called")
    public void whenCreateBankAccountMethodCalled() {
        try {
            actual = bankAccountService.createBankAccount(bankAccountRequest);
        } catch (AccountNumberUniqueException | DriverBankAccountException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the created bank account")
    public void thenResponseShouldContainCreatedBankAccountDetails() {
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(eq(bankAccount), any()))
                .thenReturn(expected);

        bankAccount = bankAccountRepository.save(bankAccount);
        expected = bankAccountMapper.mapBankAccountToBankAccountResponse(eq(bankAccount), any());

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The AccountNumberUniqueException should be thrown with message {string}")
    public void thenAccountNumberUniqueExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Then("The DriverBankAccountException should be thrown with message {string}")
    public void theDriverBankAccountExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("There is a bank account with id {long}")
    public void thereIsABankAccountWithId(long id) {
        bankAccount = TestBankAccountUtil.getFirstBankAccount();
        expected = TestBankAccountUtil.getFirstBankAccountResponse();

        when(bankAccountRepository.findById(id))
                .thenReturn(Optional.of(bankAccount));
        when(modelMapper.map(bankAccount, BankAccountResponse.class))
                .thenReturn(expected);

        assertTrue(bankAccountRepository.findById(id).isPresent());
    }

    @Given("There is no bank account with id {long}")
    public void thereIsNoBankAccountWithId(long id) {
        bankAccount = TestBankAccountUtil.getFirstBankAccount();
        bankAccount.setId(id);

        when(bankAccountRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<BankAccount> bankAccount = bankAccountRepository.findById(id);
        assertFalse(bankAccount.isPresent());
    }

    @When("Method getBankAccountById called with id {long}")
    public void methodGetBankAccountByIdCalledWithId(long id) {
        try {
            actual = bankAccountService.getBankAccountById(id);
        } catch (BankAccountNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of bank account with id {long}")
    public void theResponseShouldContainTheDetailsOfBankAccountWithId(long id) {
        bankAccount = bankAccountRepository.findById(id).get();
        expected = bankAccountMapper.mapBankAccountToBankAccountResponse(eq(bankAccount), any());

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The BankAccountNotFoundException should be thrown with message {string}")
    public void theBankAccountNotFoundExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("Deleting bank account with driver id {long} exists")
    public void deletingBankAccountWithIdExists(long id) {
        bankAccount = TestBankAccountUtil.getFirstBankAccount();

        when(bankAccountRepository.findById(id))
                .thenReturn(Optional.of(bankAccount));

        assertTrue(bankAccountRepository.findById(id).isPresent());
    }

    @Given("There is no bank account with driver id {long}")
    public void thereIsNoBankAccountWithDriverId(long id) {
        bankAccount = TestBankAccountUtil.getFirstBankAccount();
        bankAccount.setDriverId(id);

        when(bankAccountRepository.findByDriverId(id))
                .thenReturn(Optional.empty());

        Optional<BankAccount> bankAccount = bankAccountRepository.findByDriverId(id);
        assertFalse(bankAccount.isPresent());
    }

    @When("Method deleteBankAccount called with driver id {long}")
    public void methodDeleteBankAccountCalledWithId(long id) {
        try {
            bankAccountService.deleteBankAccount(id);
        } catch (BankAccountNotFoundException ex) {
            exception = ex;
        }
    }

    @Given("There is a bank account with id {long} to retrieval balance")
    public void thereIsABankAccountWithIdToRetrievalBalance(long id) {
        bankAccount = TestBankAccountUtil.getSecondBankAccount();
        expectedBalanceResponse = TestBankAccountUtil.getBalanceResponse();

        when(bankAccountRepository.findById(id))
                .thenReturn(Optional.of(bankAccount));

        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(id);
        assertTrue(optionalBankAccount.isPresent());
    }

    @When("Method getBankAccountBalance called with id {long}")
    public void methodGetBankAccountBalanceCalledWithId(long id) {
        try {
            actualBalanceResponse = bankAccountService.getBankAccountBalance(id);
        } catch (BankAccountNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of bank account balance")
    public void theResponseShouldContainTheDetailsOfBankAccountBalance() {
        assertThat(actualBalanceResponse).isEqualTo(expectedBalanceResponse);
    }

    @Given("There is bank account of driver with id {long} to refill")
    public void thereIsBankAccountOfDriverWithIdToRefill(long id) {
        refillRequest = TestBankAccountUtil.getRefillRequest();
        refillRequest.setBankUserId(id);
        bankAccount = TestBankAccountUtil.getFirstBankAccount();
        bankUserResponse = TestBankAccountUtil.getFirstBankUserResponse();
        expected = TestBankAccountUtil.getFirstBankAccountResponse();

        when(bankAccountRepository.findByDriverId(refillRequest.getBankUserId()))
                .thenReturn(Optional.of(bankAccount));
        when(driverWebClient.getDriver(refillRequest.getBankUserId()))
                .thenReturn(bankUserResponse);
        when(bankAccountRepository.save(bankAccount))
                .thenReturn(bankAccount);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccount, bankUserResponse))
                .thenReturn(expected);

        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findByDriverId(refillRequest.getBankUserId());
        assertTrue(optionalBankAccount.isPresent());
    }

    @Given("There is no bank account of driver with id {long}")
    public void thereIsNoBankAccountOfDriverWithId(long id) {
        refillRequest = TestBankAccountUtil.getRefillRequest();
        refillRequest.setBankUserId(id);

        when(bankAccountRepository.findByDriverId(id))
                .thenReturn(Optional.empty());

        assertFalse(bankAccountRepository.findByDriverId(id).isPresent());
    }

    @When("Method refillBankAccount called")
    public void methodRefillBankAccountForAccountWithIdCalled() {
        try {
            actual = bankAccountService.refillBankAccount(refillRequest);
        } catch (DriverBankAccountException | BankAccountNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of bank account of driver with id {long} after refilling")
    public void theResponseShouldContainTheDetailsOfBankAccountOfDriverWithIdAfterRefilling(long id) {
        assertEquals(expected, actual);
    }

    @Given("There is bank account with id {long} and conditions are met")
    public void thereIsBankAccountWithIdAndConditionsAreMet(long id) {
        withdrawalRequest = TestBankAccountUtil.getWithdrawalRequest();
        bankAccount = TestBankAccountUtil.getFirstBankAccount();
        bankUserResponse = TestBankAccountUtil.getFirstBankUserResponse();
        expected = TestBankAccountUtil.getFirstBankAccountResponse();
        BankCardResponse defaultBankCard = TestBankCardUtil.getFirstBankCardResponse();
        RefillRequest refillRequest = RefillRequest.builder()
                .bankUserId(bankAccount.getDriverId())
                .sum(withdrawalRequest.getSum())
                .build();

        when(bankAccountRepository.findById(id))
                .thenReturn(Optional.of(bankAccount));
        when(driverWebClient.getDriver(bankAccount.getDriverId()))
                .thenReturn(bankUserResponse);
        when(bankCardService.refillBankCard(defaultBankCard.getId(), refillRequest))
                .thenReturn(TestBankCardUtil.getFirstBankCardResponse());
        when(bankAccountRepository.save(bankAccount))
                .thenReturn(bankAccount);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccount, bankUserResponse))
                .thenReturn(expected);
        when(bankCardService.getDefaultBankCard(bankAccount.getDriverId(), BankUser.DRIVER))
                .thenReturn(defaultBankCard);

        assertTrue(bankAccountRepository.findById(id).isPresent());
    }

    @When("Method withdrawalPaymentFromBankAccount for account with id {long} called")
    public void methodWithdrawalPaymentFromBankAccountForAccountWithIdCalled(long id) {
        try {
            actual = bankAccountService.withdrawalFromBankAccount(id, withdrawalRequest);
        } catch (BankAccountNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of bank account after withdrawal money")
    public void theResponseShouldContainTheDetailsOfBankAccountWithIdAfterWithdrawalMoney() {
        assertEquals(expected, actual);
    }

    @Given("There are active accounts in the system in page {int} with size {int} and sort by {string}")
    public void thereAreActiveAccountsInTheSystemInPageWithSizeAndSortBy(int page, int size, String sortBy) {
        bankUserResponse = TestBankAccountUtil.getFirstBankUserResponse();
        List<BankAccount> activeBankAccounts = TestBankAccountUtil.getBankAccounts();
        List<BankAccountResponse> expectedResponses = TestBankAccountUtil.getBankAccountResponses();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankAccount> mockBankAccountPage = new PageImpl<>(activeBankAccounts, pageable, activeBankAccounts.size());

        doNothing()
                .when(fieldValidator)
                .checkSortField(any(), eq(sortBy));
        when(bankAccountRepository.findAll(pageable))
                .thenReturn(mockBankAccountPage);
        when(driverWebClient.getDriver(activeBankAccounts.get(0).getDriverId()))
                .thenReturn(bankUserResponse);
        when(driverWebClient.getDriver(activeBankAccounts.get(1).getDriverId()))
                .thenReturn(bankUserResponse);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(activeBankAccounts.get(0), bankUserResponse))
                .thenReturn(expectedResponses.get(0));
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(activeBankAccounts.get(1), bankUserResponse))
                .thenReturn(expectedResponses.get(1));

        Page<BankAccount> bankAccountPage = bankAccountRepository.findAll(pageable);
        assertTrue(bankAccountPage.hasContent());
    }

    @When("Method getAllActiveBankAccounts called with page {int}, size {int}, and sort by {string}")
    public void methodGetAllActiveBankAccountsCalledWithPageSizeAndSortBy(int page, int size, String sortBy) {
        try {
            actualPageResponse = bankAccountService.getAllActiveBankAccounts(page, size, sortBy);
        } catch (IncorrectFieldNameException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain a page of active bank accounts number {int} with size {int}")
    public void theResponseShouldContainAPageOfActiveBankAccountsNumberWithSize(int page, int size) {
        List<BankAccountResponse> expectedResponses = TestBankAccountUtil.getBankAccountResponses();

        BankAccountPageResponse expectedPageResponse = BankAccountPageResponse.builder()
                .bankAccounts(expectedResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        assertEquals(expectedPageResponse, actualPageResponse);
    }

    @Given("There are accounts in the system in page {int} with size {int} and sort by {string}")
    public void thereAreAccountsInTheSystemInPageWithSizeAndSortBy(int page, int size, String sortBy) {
        bankUserResponse = TestBankAccountUtil.getFirstBankUserResponse();
        List<BankAccount> bankAccounts = TestBankAccountUtil.getBankAccounts();
        List<BankAccountResponse> expectedResponses = TestBankAccountUtil.getBankAccountResponses();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankAccount> mockBankAccountPage = new PageImpl<>(bankAccounts, pageable, bankAccounts.size());

        doNothing()
                .when(fieldValidator)
                .checkSortField(any(), eq(sortBy));
        when(bankAccountRepository.findAll(pageable))
                .thenReturn(mockBankAccountPage);
        when(driverWebClient.getDriver(bankAccounts.get(0).getDriverId()))
                .thenReturn(bankUserResponse);
        when(driverWebClient.getDriver(bankAccounts.get(1).getDriverId()))
                .thenReturn(bankUserResponse);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccounts.get(0), bankUserResponse))
                .thenReturn(expectedResponses.get(0));
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccounts.get(1), bankUserResponse))
                .thenReturn(expectedResponses.get(1));

        Page<BankAccount> bankAccountPage = bankAccountRepository.findAll(pageable);
        assertTrue(bankAccountPage.hasContent());
    }

    @When("Method getAllBankAccounts called with page {int}, size {int}, and sort by {string}")
    public void methodGetAllBankAccountsCalledWithPageSizeAndSortBy(int page, int size, String sortBy) {
        try {
            actualPageResponse = bankAccountService.getAllBankAccounts(page, size, sortBy);
        } catch (IncorrectFieldNameException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain a page of bank accounts number {int} with size {int}")
    public void theResponseShouldContainAPageOfBankAccountsNumberWithSize(int page, int size) {
        List<BankAccountResponse> expectedResponses = TestBankAccountUtil.getBankAccountResponses();

        BankAccountPageResponse expectedPageResponse = BankAccountPageResponse.builder()
                .bankAccounts(expectedResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        assertEquals(expectedPageResponse, actualPageResponse);
    }
}
