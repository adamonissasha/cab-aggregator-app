package com.example.bankservice.integration;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.UpdateBankCardRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankCardPageResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.dto.response.ValidationErrorResponse;
import com.example.bankservice.integration.client.BankCardClientTest;
import com.example.bankservice.repository.BankCardRepository;
import com.example.bankservice.util.TestBankCardUtil;
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
public class BankCardControllerTest {
    @LocalServerPort
    private int port;

    private final BankCardRepository bankCardRepository;

    private final BankCardClientTest bankCardClientTest;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    public BankCardControllerTest(BankCardRepository bankCardRepository, BankCardClientTest bankCardClientTest) {
        this.bankCardRepository = bankCardRepository;
        this.bankCardClientTest = bankCardClientTest;
    }

    @Test
    void createBankCard_WhenNumberUniqueAndDataValid_ShouldReturnBankCardResponse() {
        BankCardRequest bankCardRequest = TestBankCardUtil.getUniqueBankCardRequest();
        BankCardResponse expected = TestBankCardUtil.getNewBankCardResponse();

        BankCardResponse actual =
                bankCardClientTest.createBankCardWhenNumberUniqueAndDataValidRequest(port, bankCardRequest);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);

        deleteBankCardAfterTest(actual.getId());
    }

    @Test
    void createBankCard_WhenCardNumberAlreadyExists_ShouldReturnConflictResponse() {
        BankCardRequest bankCardRequest = TestBankCardUtil.getBankCardRequestWithExistingNumber();
        ExceptionResponse expected = TestBankCardUtil.getCardNumberExistsExceptionResponse();

        ExceptionResponse actual =
                bankCardClientTest.createBankCardWhenCardNumberAlreadyExistsRequest(port, bankCardRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createBankCard_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        BankCardRequest bankCardRequest = TestBankCardUtil.getBankCardRequestWithInvalidData();
        ValidationErrorResponse expected = TestBankCardUtil.getValidationErrorResponse();

        ValidationErrorResponse actual =
                bankCardClientTest.createBankCardWhenDataNotValidRequest(port, bankCardRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editBankCard_WhenValidData_ShouldReturnBankCardResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        UpdateBankCardRequest bankCardRequest = TestBankCardUtil.getUpdateBankCardRequest();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        BankCardResponse actual =
                bankCardClientTest.editBankCardWhenValidDataRequest(port, bankCardRequest, bankCardId);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editBankCard_WhenInvalidData_ShouldReturnBadRequestResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        BankCardRequest bankCardRequest = TestBankCardUtil.getBankCardRequestWithInvalidData();
        ValidationErrorResponse expected = TestBankCardUtil.getEditValidationErrorResponse();

        ValidationErrorResponse actual =
                bankCardClientTest.editBankCardWhenInvalidDataRequest(port, bankCardRequest, bankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editBankCard_WhenBankCardNotFound_ShouldReturnNotFoundResponse() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        BankCardRequest bankCardRequest = TestBankCardUtil.getBankCardRequest();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                bankCardClientTest.editBankCardWhenBankCardNotFoundRequest(port, bankCardRequest, invalidBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankCardById_WhenBankCardExists_ShouldReturnBankCardResponse() {
        Long existingBankCardId = TestBankCardUtil.getBankCardId();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        BankCardResponse actual =
                bankCardClientTest.getBankCardByIdWhenBankCardExistsRequest(port, existingBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankCardById_WhenBankCardNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                bankCardClientTest.getBankCardByIdWhenBankCardNotExistsRequest(port, invalidBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllBankCards_ShouldReturnBankCardPageResponse() {
        Long bankUserId = 3L;
        int page = TestBankCardUtil.getPageNumber();
        int size = TestBankCardUtil.getPageSize();
        String sortBy = TestBankCardUtil.getCorrectSortField();
        BankCardPageResponse expected = TestBankCardUtil.getBankCardPageResponse();

        BankCardPageResponse actual = bankCardClientTest.getAllBankCardsRequest(port, page, size, sortBy, bankUserId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllCars_WhenIncorrectField_ShouldReturnExceptionResponse() {
        Long bankUserId = 3L;
        int page = TestBankCardUtil.getPageNumber();
        int size = TestBankCardUtil.getPageSize();
        String sortBy = TestBankCardUtil.getIncorrectSortField();
        ExceptionResponse expected = TestBankCardUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual =
                bankCardClientTest.getAllBankCardsWhenIncorrectFieldRequest(port, page, size, sortBy, bankUserId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteBankCard_WhenBankCardExists_ShouldReturnNotFoundAfterDeletion() {
        Long existingBankCardId = TestBankCardUtil.getBankCardId();

        bankCardClientTest.deleteBankCardWhenBankCardExistsRequest(port, existingBankCardId);
    }

    @Test
    void deleteBankCard_WhenBankCardNotExists_ShouldReturnNotFound() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();

        bankCardClientTest.deleteBankCardWhenBankCardNotExistsRequest(port, invalidBankCardId);
    }

    @Test
    void deleteBankUserCards_ShouldReturnOkResponse() {
        Long existingBankCardId = TestBankCardUtil.getBankCardId();

        bankCardClientTest.deleteBankUserCardsRequest(port, existingBankCardId);
    }

    @Test
    void makeBankCardDefault_WhenBankCardExists_ShouldReturnBankCardResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        BankCardResponse actual = bankCardClientTest.makeBankCardDefaultWhenBankCardExistsRequest(port, bankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void makeBankCardDefault_WhenBankCardNotExists_ShouldReturnNotFound() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                bankCardClientTest.makeBankCardDefaultWhenBankCardNotExistsRequest(port, invalidBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getDefaultBankCard_WhenDefaultBankCardExists_ShouldReturnBankCardResponse() {
        Long bankUserId = 3L;
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        BankCardResponse actual =
                bankCardClientTest.getDefaultBankCardWhenDefaultBankCardExistsRequest(port, bankUserId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getDefaultBankCard_WhenDefaultBankCardNotExists_ShouldReturnNotFoundResponse() {
        Long bankUserId = 9L;
        ExceptionResponse expected = TestBankCardUtil.getDefaultBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                bankCardClientTest.getDefaultBankCardWhenDefaultBankCardNotExistsRequest(port, bankUserId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankCardBalance_WhenBankCardExists_ShouldReturnBalanceResponse() {
        Long existingBankCardId = TestBankCardUtil.getBankCardId();
        BalanceResponse expected = TestBankCardUtil.getBalanceResponse();

        BalanceResponse actual =
                bankCardClientTest.getBankCardBalanceWhenBankCardExistsRequest(port, existingBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankCardBalance_WhenBankCardNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                bankCardClientTest.getBankCardBalanceWhenBankCardNotExistsRequest(port, invalidBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void refillBankCard_WhenBankCardExists_ShouldReturnBankCardResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        RefillRequest refillRequest = TestBankCardUtil.getRefillRequest();
        BankCardResponse expected = TestBankCardUtil.getRefillBankCardResponse();

        BankCardResponse actual =
                bankCardClientTest.refillBankCardWhenBankCardExistsRequest(port, bankCardId, refillRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void refillBankCard_WhenBankCardNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        RefillRequest refillRequest = TestBankCardUtil.getRefillRequest();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                bankCardClientTest.refillBankCardWhenBankCardNotExistsRequest(port, invalidBankCardId, refillRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankCard_WhenBankCardExists_ShouldReturnBankCardResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        WithdrawalRequest withdrawalRequest = TestBankCardUtil.getWithdrawalRequest();
        BankCardResponse expected = TestBankCardUtil.getWithdrawalBankCardResponse();

        BankCardResponse actual =
                bankCardClientTest.withdrawalPaymentFromBankCardWhenBankCardExistsRequest(port, bankCardId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankCard_WhenSumMoreThanBalance_ShouldReturnConflictResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        WithdrawalRequest withdrawalRequest = TestBankCardUtil.getBigWithdrawalRequest();
        ExceptionResponse expected = TestBankCardUtil.getBalanceExceptionResponse();

        ExceptionResponse actual =
                bankCardClientTest.withdrawalPaymentFromBankCardWhenSumMoreThanBalanceRequest(port, bankCardId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankCard_WhenBankCardNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        WithdrawalRequest withdrawalRequest = TestBankCardUtil.getWithdrawalRequest();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                bankCardClientTest.withdrawalPaymentFromBankCardWhenBankCardNotExistsRequest(port, invalidBankCardId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    void deleteBankCardAfterTest(Long id) {
        bankCardRepository.deleteById(id);
    }
}
