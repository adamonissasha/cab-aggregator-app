package com.example.bankservice.integration;

import com.example.bankservice.dto.request.BankAccountRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankAccountPageResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.dto.response.ValidationErrorResponse;
import com.example.bankservice.repository.BankAccountHistoryRepository;
import com.example.bankservice.repository.BankAccountRepository;
import com.example.bankservice.util.TestBankAccountUtil;
import com.example.bankservice.util.client.BankAccountClientUtil;
import com.example.bankservice.webClient.DriverWebClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:sql/add-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/delete-test-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Testcontainers
@AutoConfigureWireMock
public class BankAccountControllerTest {
    @LocalServerPort
    private int port;

    private final BankAccountRepository bankAccountRepository;
    private final ObjectMapper objectMapper;
    private final BankAccountHistoryRepository bankAccountHistoryRepository;
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    public BankAccountControllerTest(BankAccountRepository bankAccountRepository,
                                     ObjectMapper objectMapper,
                                     BankAccountHistoryRepository bankAccountHistoryRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.objectMapper = objectMapper;
        this.bankAccountHistoryRepository = bankAccountHistoryRepository;
    }

    @Autowired
    private DriverWebClient driverWebClient;

    public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

    @BeforeEach
    public void setup() {
        wiremock.start();
        driverWebClient.setDriverServiceUrl("http://localhost:" + wiremock.port() + "/driver");
    }

    @Test
    void createBankAccount_WhenNumberUniqueAndDataValid_ShouldReturnBankAccountResponse() throws JsonProcessingException {
        deleteBankAccount(99L);

        BankAccountRequest bankAccountRequest = TestBankAccountUtil.getCreateBankAccountRequest();
        BankAccountResponse expected = TestBankAccountUtil.getNewBankAccountResponse();
        BankUserResponse bankUserResponse = expected.getDriver();

        wiremock.stubFor(get(urlPathEqualTo("/" + bankAccountRequest.getDriverId()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(bankUserResponse))));

        BankAccountResponse actual =
                BankAccountClientUtil.createBankAccountWhenNumberUniqueAndDataValidRequest(port, bankAccountRequest);

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
                BankAccountClientUtil.createBankAccountWhenAccountNumberAlreadyExistsRequest(port, bankAccountRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createBankAccount_WhenDriverAlreadyHasAccount_ShouldReturnConflictResponse() {
        BankAccountRequest bankAccountRequest = TestBankAccountUtil.getUniqueBankAccountRequest();
        ExceptionResponse expected = TestBankAccountUtil.getDriverAlreadyHasAccountExceptionResponse();

        ExceptionResponse actual =
                BankAccountClientUtil.createBankAccountWhenDriverAlreadyHasAccountRequest(port, bankAccountRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createBankAccount_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        BankAccountRequest bankAccountRequest = TestBankAccountUtil.getBankAccountRequestWithInvalidData();
        ValidationErrorResponse expected = TestBankAccountUtil.getValidationErrorResponse();

        ValidationErrorResponse actual =
                BankAccountClientUtil.createBankAccountWhenDataNotValidRequest(port, bankAccountRequest);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void getBankAccountById_WhenBankAccountExists_ShouldReturnBankAccountResponse() {
        Long existingBankAccountId = TestBankAccountUtil.getSecondBankAccount().getId();
        BankAccountResponse expected = TestBankAccountUtil.getSecondBankAccountResponse();

        BankAccountResponse actual =
                BankAccountClientUtil.getBankAccountByIdWhenBankAccountExistsRequest(port, existingBankAccountId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankAccountById_WhenBankAccountNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankAccountId = TestBankAccountUtil.getInvalidBankAccountId();
        ExceptionResponse expected = TestBankAccountUtil.getBankAccountNotFoundExceptionResponse();

        ExceptionResponse actual =
                BankAccountClientUtil.getBankAccountBalanceWhenBankAccountNotExistsRequest(port, invalidBankAccountId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllBankAccounts_ShouldReturnBankAccountPageResponse() {
        int page = TestBankAccountUtil.getPageNumber();
        int size = TestBankAccountUtil.getPageSize();
        String sortBy = TestBankAccountUtil.getCorrectSortField();
        BankAccountPageResponse expected = TestBankAccountUtil.getBankAccountPageResponse();

        BankAccountPageResponse actual = BankAccountClientUtil.getAllBankAccountsRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllBankAccounts_WhenIncorrectField_ShouldReturnExceptionResponse() {
        int page = TestBankAccountUtil.getPageNumber();
        int size = TestBankAccountUtil.getPageSize();
        String sortBy = TestBankAccountUtil.getIncorrectSortField();
        ExceptionResponse expected = TestBankAccountUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual =
                BankAccountClientUtil.getAllBankAccountsWhenIncorrectFieldRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllActiveBankAccounts_ShouldReturnBankAccountPageResponse() {
        int page = TestBankAccountUtil.getPageNumber();
        int size = TestBankAccountUtil.getPageSize();
        String sortBy = TestBankAccountUtil.getCorrectSortField();
        BankAccountPageResponse expected = TestBankAccountUtil.getBankAccountPageResponse();

        BankAccountPageResponse actual = BankAccountClientUtil.getAllActiveBankAccountsRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllActiveBankAccounts_WhenIncorrectField_ShouldReturnExceptionResponse() {
        int page = TestBankAccountUtil.getPageNumber();
        int size = TestBankAccountUtil.getPageSize();
        String sortBy = TestBankAccountUtil.getIncorrectSortField();
        ExceptionResponse expected = TestBankAccountUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual =
                BankAccountClientUtil.getAllActiveBankAccountWhenIncorrectFieldRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteBankAccount_WhenBankAccountExists_ShouldReturnOkResponse() {
        Long driverId = 1L;

        BankAccountClientUtil.deleteBankAccountWhenBankAccountExistsRequest(port, driverId);
    }

    @Test
    void deleteBankAccount_WhenBankAccountNotExists_ShouldReturnNotFound() {
        Long invalidBankAccountId = TestBankAccountUtil.getInvalidBankAccountId();

        BankAccountClientUtil.deleteBankAccountWhenBankAccountNotExistsRequest(port, invalidBankAccountId);
    }

    @Test
    void getBankAccountBalance_WhenBankAccountExists_ShouldReturnBalanceResponse() {
        Long existingBankAccountId = TestBankAccountUtil.getBankAccountId();
        BalanceResponse expected = TestBankAccountUtil.getBalanceResponse();

        BalanceResponse actual =
                BankAccountClientUtil.getBankAccountBalanceWhenBankAccountExistsRequest(port, existingBankAccountId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankAccountBalance_WhenBankAccountNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankAccountId = TestBankAccountUtil.getInvalidBankAccountId();
        ExceptionResponse expected = TestBankAccountUtil.getBankAccountNotFoundExceptionResponse();

        ExceptionResponse actual =
                BankAccountClientUtil.getBankAccountByIdWhenBankAccountNotExistsRequest(port, invalidBankAccountId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void refillBankAccount_WhenBankAccountExists_ShouldReturnBankAccountResponse() {
        RefillRequest refillRequest = TestBankAccountUtil.getRefillRequest();
        BankAccountResponse expected = TestBankAccountUtil.getRefillBankAccountResponse();

        BankAccountResponse actual =
                BankAccountClientUtil.refillBankAccountWhenBankAccountExistsRequest(port, refillRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void refillBankAccount_WhenBankAccountNotExists_ShouldReturnNotFoundResponse() {
        RefillRequest refillRequest = TestBankAccountUtil.getInvalidRefillRequest();
        ExceptionResponse expected = TestBankAccountUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual =
                BankAccountClientUtil.refillBankAccountWhenBankAccountNotExistsRequest(port, refillRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankAccount_WhenBankAccountExists_ShouldReturnBankAccountResponse() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        WithdrawalRequest withdrawalRequest = TestBankAccountUtil.getWithdrawalRequest();
        BankAccountResponse expected = TestBankAccountUtil.getWithdrawalBankAccountResponse();

        BankAccountResponse actual =
                BankAccountClientUtil.withdrawalPaymentFromBankAccountWhenBankAccountExistsRequest(port, bankAccountId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankAccount_WhenSumMoreThanBalance_ShouldReturnConflictResponse() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        WithdrawalRequest withdrawalRequest = TestBankAccountUtil.getLargeWithdrawalRequest();
        ExceptionResponse expected = TestBankAccountUtil.getBalanceExceptionResponse();

        ExceptionResponse actual =
                BankAccountClientUtil.withdrawalPaymentFromBankAccountWhenSumMoreThanBalanceRequest(port, bankAccountId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankAccount_WhenSumIsOutsideBorder_ShouldReturnConflictResponse() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        WithdrawalRequest withdrawalRequest = TestBankAccountUtil.getOutsideBorderWithdrawalRequest();
        ExceptionResponse expected = TestBankAccountUtil.getOutsideBorderExceptionResponse();

        ExceptionResponse actual =
                BankAccountClientUtil.withdrawalPaymentFromBankAccountWhenSumIsOutsideBorderRequest(port, bankAccountId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankAccount_WhenBankAccountNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankAccountId = TestBankAccountUtil.getInvalidBankAccountId();
        WithdrawalRequest withdrawalRequest = TestBankAccountUtil.getWithdrawalRequest();
        ExceptionResponse expected = TestBankAccountUtil.getBankAccountNotFoundExceptionResponse();

        ExceptionResponse actual =
                BankAccountClientUtil.withdrawalPaymentFromBankAccountWhenBankAccountNotExistsRequest(port, invalidBankAccountId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    void deleteBankAccount(Long id) {
        bankAccountHistoryRepository.deleteAllByBankAccountId(id);
        bankAccountRepository.deleteById(id);
    }
}
