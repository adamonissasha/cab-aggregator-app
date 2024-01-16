package com.example.bankservice.integration;

import com.example.bankservice.dto.request.BankAccountRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankAccountPageResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.dto.response.ValidationErrorResponse;
import com.example.bankservice.integration.client.BankAccountClientTest;
import com.example.bankservice.repository.BankAccountHistoryRepository;
import com.example.bankservice.repository.BankAccountRepository;
import com.example.bankservice.util.TestBankAccountUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:sql/add-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/delete-test-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Testcontainers
public class BankAccountControllerTest {
    @LocalServerPort
    private int port;

    private final BankAccountRepository bankAccountRepository;

    private final BankAccountHistoryRepository bankAccountHistoryRepository;

    private final BankAccountClientTest bankAccountClientTest;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    public BankAccountControllerTest(BankAccountRepository bankAccountRepository, BankAccountHistoryRepository bankAccountHistoryRepository, BankAccountClientTest bankAccountClientTest) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountHistoryRepository = bankAccountHistoryRepository;
        this.bankAccountClientTest = bankAccountClientTest;
    }

    @Test
    void createBankAccount_WhenNumberUniqueAndDataValid_ShouldReturnBankAccountResponse() {
        deleteBankAccount(99L);

        BankAccountRequest bankAccountRequest = TestBankAccountUtil.getCreateBankAccountRequest();
        BankAccountResponse expected = TestBankAccountUtil.getNewBankAccountResponse();

        BankAccountResponse actual =
                bankAccountClientTest.createBankAccountWhenNumberUniqueAndDataValidRequest(port, bankAccountRequest);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);

        deleteBankAccount(actual.getId());
    }

    @Test
    void createBankAccount_WhenAccountNumberAlreadyExists_ShouldReturnConflictResponse() {
        BankAccountRequest bankAccountRequest = TestBankAccountUtil.getBankAccountRequestWithExistingNumber();
        ExceptionResponse expected = TestBankAccountUtil.getBankAccountNumberExistsExceptionResponse();

        ExceptionResponse actual =
                bankAccountClientTest.createBankAccountWhenAccountNumberAlreadyExistsRequest(port, bankAccountRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createBankAccount_WhenDriverAlreadyHasAccount_ShouldReturnConflictResponse() {
        BankAccountRequest bankAccountRequest = TestBankAccountUtil.getUniqueBankAccountRequest();
        ExceptionResponse expected = TestBankAccountUtil.getDriverAlreadyHasAccountExceptionResponse();

        ExceptionResponse actual =
                bankAccountClientTest.createBankAccountWhenDriverAlreadyHasAccountRequest(port, bankAccountRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createBankAccount_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        BankAccountRequest bankAccountRequest = TestBankAccountUtil.getBankAccountRequestWithInvalidData();
        ValidationErrorResponse expected = TestBankAccountUtil.getValidationErrorResponse();

        ValidationErrorResponse actual =
                bankAccountClientTest.createBankAccountWhenDataNotValidRequest(port, bankAccountRequest);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void getBankAccountById_WhenBankAccountExists_ShouldReturnBankAccountResponse() {
        Long existingBankAccountId = TestBankAccountUtil.getSecondBankAccount().getId();
        BankAccountResponse expected = TestBankAccountUtil.getSecondBankAccountResponse();

        BankAccountResponse actual =
                bankAccountClientTest.getBankAccountByIdWhenBankAccountExistsRequest(port, existingBankAccountId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankAccountById_WhenBankAccountNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankAccountId = TestBankAccountUtil.getInvalidBankAccountId();
        ExceptionResponse expected = TestBankAccountUtil.getBankAccountNotFoundExceptionResponse();

        ExceptionResponse actual =
                bankAccountClientTest.getBankAccountBalanceWhenBankAccountNotExistsRequest(port, invalidBankAccountId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllBankAccounts_ShouldReturnBankAccountPageResponse() {
        int page = TestBankAccountUtil.getPageNumber();
        int size = TestBankAccountUtil.getPageSize();
        String sortBy = TestBankAccountUtil.getCorrectSortField();
        BankAccountPageResponse expected = TestBankAccountUtil.getBankAccountPageResponse();

        BankAccountPageResponse actual = bankAccountClientTest.getAllBankAccountsRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllBankAccounts_WhenIncorrectField_ShouldReturnExceptionResponse() {
        int page = TestBankAccountUtil.getPageNumber();
        int size = TestBankAccountUtil.getPageSize();
        String sortBy = TestBankAccountUtil.getIncorrectSortField();
        ExceptionResponse expected = TestBankAccountUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual =
                bankAccountClientTest.getAllBankAccountsWhenIncorrectFieldRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllActiveBankAccounts_ShouldReturnBankAccountPageResponse() {
        int page = TestBankAccountUtil.getPageNumber();
        int size = TestBankAccountUtil.getPageSize();
        String sortBy = TestBankAccountUtil.getCorrectSortField();
        BankAccountPageResponse expected = TestBankAccountUtil.getBankAccountPageResponse();

        BankAccountPageResponse actual = bankAccountClientTest.getAllActiveBankAccountsRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllActiveBankAccounts_WhenIncorrectField_ShouldReturnExceptionResponse() {
        int page = TestBankAccountUtil.getPageNumber();
        int size = TestBankAccountUtil.getPageSize();
        String sortBy = TestBankAccountUtil.getIncorrectSortField();
        ExceptionResponse expected = TestBankAccountUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual =
                bankAccountClientTest.getAllActiveBankAccountWhenIncorrectFieldRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteBankAccount_WhenBankAccountExists_ShouldReturnOkResponse() {
        Long driverId = 1L;

        bankAccountClientTest.deleteBankAccountWhenBankAccountExistsRequest(port, driverId);
    }

    @Test
    void deleteBankAccount_WhenBankAccountNotExists_ShouldReturnNotFound() {
        Long invalidBankAccountId = TestBankAccountUtil.getInvalidBankAccountId();

        bankAccountClientTest.deleteBankAccountWhenBankAccountNotExistsRequest(port, invalidBankAccountId);
    }

    @Test
    void getBankAccountBalance_WhenBankAccountExists_ShouldReturnBalanceResponse() {
        Long existingBankAccountId = TestBankAccountUtil.getBankAccountId();
        BalanceResponse expected = TestBankAccountUtil.getBalanceResponse();

        BalanceResponse actual =
                bankAccountClientTest.getBankAccountBalanceWhenBankAccountExistsRequest(port, existingBankAccountId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankAccountBalance_WhenBankAccountNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankAccountId = TestBankAccountUtil.getInvalidBankAccountId();
        ExceptionResponse expected = TestBankAccountUtil.getBankAccountNotFoundExceptionResponse();

        ExceptionResponse actual =
                bankAccountClientTest.getBankAccountByIdWhenBankAccountNotExistsRequest(port, invalidBankAccountId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void refillBankAccount_WhenBankAccountExists_ShouldReturnBankAccountResponse() {
        RefillRequest refillRequest = TestBankAccountUtil.getRefillRequest();
        BankAccountResponse expected = TestBankAccountUtil.getRefillBankAccountResponse();

        BankAccountResponse actual =
                bankAccountClientTest.refillBankAccountWhenBankAccountExistsRequest(port, refillRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void refillBankAccount_WhenBankAccountNotExists_ShouldReturnNotFoundResponse() {
        RefillRequest refillRequest = TestBankAccountUtil.getInvalidRefillRequest();
        ExceptionResponse expected = TestBankAccountUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual =
                bankAccountClientTest.refillBankAccountWhenBankAccountNotExistsRequest(port, refillRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankAccount_WhenBankAccountExists_ShouldReturnBankAccountResponse() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        WithdrawalRequest withdrawalRequest = TestBankAccountUtil.getWithdrawalRequest();
        BankAccountResponse expected = TestBankAccountUtil.getWithdrawalBankAccountResponse();

        BankAccountResponse actual =
                bankAccountClientTest.withdrawalPaymentFromBankAccountWhenBankAccountExistsRequest(port, bankAccountId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankAccount_WhenSumMoreThanBalance_ShouldReturnConflictResponse() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        WithdrawalRequest withdrawalRequest = TestBankAccountUtil.getLargeWithdrawalRequest();
        ExceptionResponse expected = TestBankAccountUtil.getBalanceExceptionResponse();

        ExceptionResponse actual =
                bankAccountClientTest.withdrawalPaymentFromBankAccountWhenSumMoreThanBalanceRequest(port, bankAccountId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankAccount_WhenSumIsOutsideBorder_ShouldReturnConflictResponse() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        WithdrawalRequest withdrawalRequest = TestBankAccountUtil.getOutsideBorderWithdrawalRequest();
        ExceptionResponse expected = TestBankAccountUtil.getOutsideBorderExceptionResponse();

        ExceptionResponse actual =
                bankAccountClientTest.withdrawalPaymentFromBankAccountWhenSumIsOutsideBorderRequest(port, bankAccountId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankAccount_WhenBankAccountNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankAccountId = TestBankAccountUtil.getInvalidBankAccountId();
        WithdrawalRequest withdrawalRequest = TestBankAccountUtil.getWithdrawalRequest();
        ExceptionResponse expected = TestBankAccountUtil.getBankAccountNotFoundExceptionResponse();

        ExceptionResponse actual =
                bankAccountClientTest.withdrawalPaymentFromBankAccountWhenBankAccountNotExistsRequest(port, invalidBankAccountId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    void deleteBankAccount(Long id) {
        bankAccountHistoryRepository.deleteAllByBankAccountId(id);
        bankAccountRepository.deleteById(id);
    }
}
