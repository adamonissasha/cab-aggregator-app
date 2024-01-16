package com.example.bankservice.integration;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryPageResponse;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.integration.client.BankAccountHistoryClientTest;
import com.example.bankservice.repository.BankAccountHistoryRepository;
import com.example.bankservice.util.TestBankAccountHistoryUtil;
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
public class BankAccountHistoryControllerTest {
    @LocalServerPort
    private int port;

    private final BankAccountHistoryRepository bankAccountHistoryRepository;

    private final BankAccountHistoryClientTest bankAccountHistoryClientTest;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    public BankAccountHistoryControllerTest(BankAccountHistoryRepository bankAccountHistoryRepository,
                                            BankAccountHistoryClientTest bankAccountHistoryClientTest) {
        this.bankAccountHistoryRepository = bankAccountHistoryRepository;
        this.bankAccountHistoryClientTest = bankAccountHistoryClientTest;
    }

    @Test
    void createBankAccountHistory_WhenNumberUniqueAndDataValid_ShouldReturnBankAccountHistoryResponse() {
        Long bankAccountId = TestBankAccountHistoryUtil.getBankAccountId();
        BankAccountHistoryRequest bankAccountHistoryRequest = TestBankAccountHistoryUtil.getBankAccountHistoryRequest();
        BankAccountHistoryResponse expected = TestBankAccountHistoryUtil.getFirstBankAccountHistoryResponse();

        BankAccountHistoryResponse actual =
                bankAccountHistoryClientTest.createBankAccountHistoryRequest(port, bankAccountHistoryRequest, bankAccountId);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "operationDateTime")
                .isEqualTo(expected);

        deleteBankAccountHistory(actual.getId());
    }

    @Test
    void getAllBankAccountHistoryRecords_ShouldReturnBankAccountHistoryPageResponse() {
        Long bankAccountId = TestBankAccountHistoryUtil.getBankAccountId();
        int page = TestBankAccountHistoryUtil.getPageNumber();
        int size = TestBankAccountHistoryUtil.getPageSize();
        String sortBy = TestBankAccountHistoryUtil.getCorrectSortField();
        BankAccountHistoryPageResponse expected = TestBankAccountHistoryUtil.getBankAccountHistoryPageResponse();

        BankAccountHistoryPageResponse actual =
                bankAccountHistoryClientTest.getAllBankAccountHistoryRecordsRequest(port, page, size, sortBy, bankAccountId);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("bankAccountHistoryRecords")
                .isEqualTo(expected);

        assertThat(actual.getBankAccountHistoryRecords())
                .usingElementComparator(
                        (a, e) ->
                                a.getOperationDateTime().isEqual(e.getOperationDateTime())
                                        ? a.getOperationDateTime().compareTo(e.getOperationDateTime())
                                        : 0
                )
                .containsExactlyInAnyOrderElementsOf(expected.getBankAccountHistoryRecords());
    }

    @Test
    void getAllBankAccountHistoryRecords_WhenIncorrectField_ShouldReturnExceptionResponse() {
        Long bankAccountId = TestBankAccountHistoryUtil.getBankAccountId();
        int page = TestBankAccountHistoryUtil.getPageNumber();
        int size = TestBankAccountHistoryUtil.getPageSize();
        String sortBy = TestBankAccountHistoryUtil.getIncorrectSortField();
        ExceptionResponse expected = TestBankAccountHistoryUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual =
                bankAccountHistoryClientTest.getAllBankAccountHistoryRecordsWhenIncorrectFieldRequest(port, page, size,
                        sortBy, bankAccountId);

        assertThat(actual).isEqualTo(expected);
    }

    void deleteBankAccountHistory(Long id) {
        bankAccountHistoryRepository.deleteById(id);
    }
}
