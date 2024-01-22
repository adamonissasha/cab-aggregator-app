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
import com.example.bankservice.repository.BankCardRepository;
import com.example.bankservice.util.TestBankCardUtil;
import com.example.bankservice.util.client.BankCardClientUtil;
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

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    public BankCardControllerTest(BankCardRepository bankCardRepository) {
        this.bankCardRepository = bankCardRepository;
    }

    @Test
    void createBankCard_WhenNumberUniqueAndDataValid_ShouldReturnBankCardResponse() {
        BankCardRequest bankCardRequest = TestBankCardUtil.getUniqueBankCardRequest();
        BankCardResponse expected = TestBankCardUtil.getNewBankCardResponse();

        BankCardResponse actual =
                BankCardClientUtil.createBankCardWhenNumberUniqueAndDataValidRequest(port, bankCardRequest);

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
                BankCardClientUtil.createBankCardWhenCardNumberAlreadyExistsRequest(port, bankCardRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createBankCard_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        BankCardRequest bankCardRequest = TestBankCardUtil.getBankCardRequestWithInvalidData();
        ValidationErrorResponse expected = TestBankCardUtil.getValidationErrorResponse();

        ValidationErrorResponse actual =
                BankCardClientUtil.createBankCardWhenDataNotValidRequest(port, bankCardRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editBankCard_WhenValidData_ShouldReturnBankCardResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        UpdateBankCardRequest bankCardRequest = TestBankCardUtil.getUpdateBankCardRequest();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        BankCardResponse actual =
                BankCardClientUtil.editBankCardWhenValidDataRequest(port, bankCardRequest, bankCardId);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editBankCard_WhenInvalidData_ShouldReturnBadRequestResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        BankCardRequest bankCardRequest = TestBankCardUtil.getBankCardRequestWithInvalidData();
        ValidationErrorResponse expected = TestBankCardUtil.getEditValidationErrorResponse();

        ValidationErrorResponse actual =
                BankCardClientUtil.editBankCardWhenInvalidDataRequest(port, bankCardRequest, bankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editBankCard_WhenBankCardNotFound_ShouldReturnNotFoundResponse() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        BankCardRequest bankCardRequest = TestBankCardUtil.getBankCardRequest();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                BankCardClientUtil.editBankCardWhenBankCardNotFoundRequest(port, bankCardRequest, invalidBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankCardById_WhenBankCardExists_ShouldReturnBankCardResponse() {
        Long existingBankCardId = TestBankCardUtil.getBankCardId();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        BankCardResponse actual =
                BankCardClientUtil.getBankCardByIdWhenBankCardExistsRequest(port, existingBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankCardById_WhenBankCardNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                BankCardClientUtil.getBankCardByIdWhenBankCardNotExistsRequest(port, invalidBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllBankCards_ShouldReturnBankCardPageResponse() {
        Long bankUserId = 3L;
        int page = TestBankCardUtil.getPageNumber();
        int size = TestBankCardUtil.getPageSize();
        String sortBy = TestBankCardUtil.getCorrectSortField();
        BankCardPageResponse expected = TestBankCardUtil.getBankCardPageResponse();

        BankCardPageResponse actual = BankCardClientUtil.getAllBankCardsRequest(port, page, size, sortBy, bankUserId);

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
                BankCardClientUtil.getAllBankCardsWhenIncorrectFieldRequest(port, page, size, sortBy, bankUserId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteBankCard_WhenBankCardExists_ShouldReturnNotFoundAfterDeletion() {
        Long existingBankCardId = TestBankCardUtil.getBankCardId();

        BankCardClientUtil.deleteBankCardWhenBankCardExistsRequest(port, existingBankCardId);
    }

    @Test
    void deleteBankCard_WhenBankCardNotExists_ShouldReturnNotFound() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();

        BankCardClientUtil.deleteBankCardWhenBankCardNotExistsRequest(port, invalidBankCardId);
    }

    @Test
    void deleteBankUserCards_ShouldReturnOkResponse() {
        Long existingBankCardId = TestBankCardUtil.getBankCardId();

        BankCardClientUtil.deleteBankUserCardsRequest(port, existingBankCardId);
    }

    @Test
    void makeBankCardDefault_WhenBankCardExists_ShouldReturnBankCardResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        BankCardResponse actual = BankCardClientUtil.makeBankCardDefaultWhenBankCardExistsRequest(port, bankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void makeBankCardDefault_WhenBankCardNotExists_ShouldReturnNotFound() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                BankCardClientUtil.makeBankCardDefaultWhenBankCardNotExistsRequest(port, invalidBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getDefaultBankCard_WhenDefaultBankCardExists_ShouldReturnBankCardResponse() {
        Long bankUserId = 3L;
        BankCardResponse expected = TestBankCardUtil.getFirstBankCardResponse();

        BankCardResponse actual =
                BankCardClientUtil.getDefaultBankCardWhenDefaultBankCardExistsRequest(port, bankUserId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getDefaultBankCard_WhenDefaultBankCardNotExists_ShouldReturnNotFoundResponse() {
        Long bankUserId = 9L;
        ExceptionResponse expected = TestBankCardUtil.getDefaultBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                BankCardClientUtil.getDefaultBankCardWhenDefaultBankCardNotExistsRequest(port, bankUserId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankCardBalance_WhenBankCardExists_ShouldReturnBalanceResponse() {
        Long existingBankCardId = TestBankCardUtil.getBankCardId();
        BalanceResponse expected = TestBankCardUtil.getBalanceResponse();

        BalanceResponse actual =
                BankCardClientUtil.getBankCardBalanceWhenBankCardExistsRequest(port, existingBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBankCardBalance_WhenBankCardNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                BankCardClientUtil.getBankCardBalanceWhenBankCardNotExistsRequest(port, invalidBankCardId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void refillBankCard_WhenBankCardExists_ShouldReturnBankCardResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        RefillRequest refillRequest = TestBankCardUtil.getRefillRequest();
        BankCardResponse expected = TestBankCardUtil.getRefillBankCardResponse();

        BankCardResponse actual =
                BankCardClientUtil.refillBankCardWhenBankCardExistsRequest(port, bankCardId, refillRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void refillBankCard_WhenBankCardNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        RefillRequest refillRequest = TestBankCardUtil.getRefillRequest();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                BankCardClientUtil.refillBankCardWhenBankCardNotExistsRequest(port, invalidBankCardId, refillRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankCard_WhenBankCardExists_ShouldReturnBankCardResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        WithdrawalRequest withdrawalRequest = TestBankCardUtil.getWithdrawalRequest();
        BankCardResponse expected = TestBankCardUtil.getWithdrawalBankCardResponse();

        BankCardResponse actual =
                BankCardClientUtil.withdrawalPaymentFromBankCardWhenBankCardExistsRequest(port, bankCardId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankCard_WhenSumMoreThanBalance_ShouldReturnConflictResponse() {
        Long bankCardId = TestBankCardUtil.getBankCardId();
        WithdrawalRequest withdrawalRequest = TestBankCardUtil.getBigWithdrawalRequest();
        ExceptionResponse expected = TestBankCardUtil.getBalanceExceptionResponse();

        ExceptionResponse actual =
                BankCardClientUtil.withdrawalPaymentFromBankCardWhenSumMoreThanBalanceRequest(port, bankCardId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalPaymentFromBankCard_WhenBankCardNotExists_ShouldReturnNotFoundResponse() {
        Long invalidBankCardId = TestBankCardUtil.getInvalidBankCardId();
        WithdrawalRequest withdrawalRequest = TestBankCardUtil.getWithdrawalRequest();
        ExceptionResponse expected = TestBankCardUtil.getBankCardNotFoundExceptionResponse();

        ExceptionResponse actual =
                BankCardClientUtil.withdrawalPaymentFromBankCardWhenBankCardNotExistsRequest(port, invalidBankCardId, withdrawalRequest);

        assertThat(actual).isEqualTo(expected);
    }

    void deleteBankCardAfterTest(Long id) {
        bankCardRepository.deleteById(id);
    }
}
