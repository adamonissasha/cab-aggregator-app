package com.example.bankservice.integration;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryPageResponse;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.dto.response.ExceptionResponse;
import com.example.bankservice.repository.BankAccountHistoryRepository;
import com.example.bankservice.util.TestBankAccountHistoryUtil;
import com.example.bankservice.util.client.BankAccountHistoryClientUtil;
import com.example.bankservice.webClient.DriverWebClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
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
public class BankAccountHistoryControllerTest {
    @LocalServerPort
    private int port;

    private final BankAccountHistoryRepository bankAccountHistoryRepository;
    private final ObjectMapper objectMapper;
    private final DriverWebClient driverWebClient;

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
                                            ObjectMapper objectMapper,
                                            DriverWebClient driverWebClient) {
        this.bankAccountHistoryRepository = bankAccountHistoryRepository;
        this.objectMapper = objectMapper;
        this.driverWebClient = driverWebClient;
    }

    public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

    @BeforeEach
    public void setup() {
        wiremock.start();
        driverWebClient.setDriverServiceUrl("http://localhost:" + wiremock.port() + "/driver");
    }

    @Test
    void createBankAccountHistory_WhenNumberUniqueAndDataValid_ShouldReturnBankAccountHistoryResponse() throws JsonProcessingException {
        Long bankAccountId = TestBankAccountHistoryUtil.getBankAccountId();
        BankAccountHistoryRequest bankAccountHistoryRequest = TestBankAccountHistoryUtil.getBankAccountHistoryRequest();
        BankAccountHistoryResponse expected = TestBankAccountHistoryUtil.getFirstBankAccountHistoryResponse();
        BankUserResponse bankUserResponse = expected.getBankAccount().getDriver();

        wiremock.stubFor(get(urlPathEqualTo("/driver/" + bankUserResponse.getId()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(bankUserResponse))));

        BankAccountHistoryResponse actual =
                BankAccountHistoryClientUtil.createBankAccountHistoryRequest(port, bankAccountHistoryRequest, bankAccountId);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "operationDateTime")
                .isEqualTo(expected);

        deleteBankAccountHistory(actual.getId());
    }

    @Test
    void getAllBankAccountHistoryRecords_ShouldReturnBankAccountHistoryPageResponse() throws JsonProcessingException {
        Long bankAccountId = TestBankAccountHistoryUtil.getBankAccountId();
        int page = TestBankAccountHistoryUtil.getPageNumber();
        int size = TestBankAccountHistoryUtil.getPageSize();
        String sortBy = TestBankAccountHistoryUtil.getCorrectSortField();
        BankAccountHistoryPageResponse expected = TestBankAccountHistoryUtil.getBankAccountHistoryPageResponse();
        BankUserResponse firstBankUserResponse = expected.getBankAccountHistoryRecords().get(0).getBankAccount().getDriver();
        BankUserResponse secondBankUserResponse = expected.getBankAccountHistoryRecords().get(1).getBankAccount().getDriver();

        wiremock.stubFor(get(urlPathEqualTo("/driver/" + firstBankUserResponse.getId()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(firstBankUserResponse))));

        wiremock.stubFor(get(urlPathEqualTo("/driver/" + secondBankUserResponse.getId()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(secondBankUserResponse))));

        BankAccountHistoryPageResponse actual =
                BankAccountHistoryClientUtil.getAllBankAccountHistoryRecordsRequest(port, page, size, sortBy, bankAccountId);

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
                BankAccountHistoryClientUtil.getAllBankAccountHistoryRecordsWhenIncorrectFieldRequest(port, page, size,
                        sortBy, bankAccountId);

        assertThat(actual).isEqualTo(expected);
    }

    void deleteBankAccountHistory(Long id) {
        bankAccountHistoryRepository.deleteById(id);
    }
}
