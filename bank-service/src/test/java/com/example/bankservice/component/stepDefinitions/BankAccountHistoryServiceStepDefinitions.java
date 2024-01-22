package com.example.bankservice.component.stepDefinitions;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryPageResponse;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.mapper.BankAccountHistoryMapper;
import com.example.bankservice.mapper.BankAccountMapper;
import com.example.bankservice.model.BankAccount;
import com.example.bankservice.model.BankAccountHistory;
import com.example.bankservice.model.enums.Operation;
import com.example.bankservice.repository.BankAccountHistoryRepository;
import com.example.bankservice.service.impl.BankAccountHistoryServiceImpl;
import com.example.bankservice.util.FieldValidator;
import com.example.bankservice.util.TestBankAccountHistoryUtil;
import com.example.bankservice.util.TestBankAccountUtil;
import com.example.bankservice.webClient.DriverWebClient;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class BankAccountHistoryServiceStepDefinitions {
    @Mock
    private BankAccountHistoryRepository bankAccountHistoryRepository;

    @Mock
    private FieldValidator fieldValidator;

    @Mock
    private DriverWebClient driverWebClient;

    @Mock
    private BankAccountMapper bankAccountMapper;

    @Mock
    private BankAccountHistoryMapper bankAccountHistoryMapper;

    @InjectMocks
    private BankAccountHistoryServiceImpl bankAccountHistoryService;

    private BankAccountHistoryRequest bankAccountHistoryRequest;
    private BankAccountHistoryPageResponse actualBankAccountHistoryPageResponse;
    private BankAccountResponse bankAccountResponse;
    private BankAccountHistoryResponse expected;
    private BankAccountHistoryResponse actual;
    private BankAccountHistory bankAccountHistory;
    private BankUserResponse bankUserResponse;
    private LocalDateTime expectedLastWithdrawalDate;
    private LocalDateTime actualLastWithdrawalDate;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("New bank account history record for bank account with id {long}")
    public void newBankAccountHistoryRecordForBankAccountWithId(long bankAccountId) {
        bankAccountHistoryRequest = TestBankAccountHistoryUtil.getBankAccountHistoryRequest();
        bankAccountHistory = TestBankAccountHistoryUtil.getFirstBankAccountHistory();
        bankUserResponse = TestBankAccountUtil.getFirstBankUserResponse();
        bankAccountResponse = TestBankAccountUtil.getFirstBankAccountResponse();
        expected = TestBankAccountHistoryUtil.getFirstBankAccountHistoryResponse();

        when(bankAccountHistoryMapper.mapBankAccountHistoryRequestToBankAccountHistory(bankAccountId, bankAccountHistoryRequest))
                .thenReturn(bankAccountHistory);
        when(bankAccountHistoryRepository.save(bankAccountHistory))
                .thenReturn(bankAccountHistory);
        when(driverWebClient.getDriver(bankAccountId))
                .thenReturn(bankUserResponse);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(any(BankAccount.class), any(BankUserResponse.class)))
                .thenReturn(bankAccountResponse);
        when(bankAccountHistoryMapper.mapBankAccountHistoryToBankAccountHistoryResponse(bankAccountHistory, bankAccountResponse))
                .thenReturn(expected);
    }

    @When("Method createBankAccountHistoryRecord for bank account with id {long} called")
    public void methodCreateBankAccountHistoryRecordForBankAccountWithIdCalled(long bankAccountId) {
        actual = bankAccountHistoryService.createBankAccountHistoryRecord(bankAccountId, bankAccountHistoryRequest);
    }

    @Then("The response should contain the details of the created bank account history record")
    public void theResponseShouldContainTheDetailsOfTheCreatedBankAccountHistoryRecord() {
        assertEquals(expected, actual);
    }

    @Given("There are withdrawals in the system for bank account with id {long}")
    public void thereAreWithdrawalsInTheSystemForBankAccountWithId(long bankAccountId) {
        expectedLastWithdrawalDate = LocalDateTime.now();
        bankAccountHistory = TestBankAccountHistoryUtil.getFirstBankAccountHistory();

        when(bankAccountHistoryRepository.findFirstByBankAccountIdAndOperationOrderByOperationDateTimeDesc(bankAccountId, Operation.WITHDRAWAL))
                .thenReturn(Optional.of(bankAccountHistory));

        Optional<BankAccountHistory> optionalBankAccountHistory =
                bankAccountHistoryRepository.findFirstByBankAccountIdAndOperationOrderByOperationDateTimeDesc(bankAccountId, Operation.WITHDRAWAL);
        assertTrue(optionalBankAccountHistory.isPresent());
    }

    @When("Method getLastWithdrawalDate for bank account with id {long} called")
    public void methodGetLastWithdrawalDateForBankAccountWithIdCalled(long bankAccountId) {
        actualLastWithdrawalDate = bankAccountHistoryService.getLastWithdrawalDate(bankAccountId);
    }

    @Then("The response should contain the details of last withdrawal date")
    public void theResponseShouldContainTheDetailsOfLastWithdrawalDate() {
        assertThat(actualLastWithdrawalDate).isCloseTo(expectedLastWithdrawalDate, within(10, SECONDS));
    }

    @Given("There is bank account with id {long} history in the system in page {int} with size {int} and sort by {string}")
    public void thereIsBankAccountWithIdHistoryInTheSystemInPageWithSizeAndSortBy(long bankAccountId,
                                                                                  int page,
                                                                                  int size,
                                                                                  String sortBy) {
        bankUserResponse = TestBankAccountUtil.getFirstBankUserResponse();
        bankAccountResponse = TestBankAccountUtil.getFirstBankAccountResponse();
        List<BankAccountHistory> bankAccountHistoryList = TestBankAccountHistoryUtil.getBankAccountHistoryList();
        List<BankAccountHistoryResponse> expectedHistoryResponses = TestBankAccountHistoryUtil.getBankAccountHistoryResponses();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankAccountHistory> mockBankAccountHistoryPage = new PageImpl<>(bankAccountHistoryList, pageable, bankAccountHistoryList.size());

        doNothing()
                .when(fieldValidator)
                .checkSortField(any(), eq(sortBy));
        when(bankAccountHistoryRepository.findAllByBankAccountId(eq(bankAccountId), eq(pageable)))
                .thenReturn(mockBankAccountHistoryPage);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(
                any(BankAccount.class), any(BankUserResponse.class)))
                .thenReturn(bankAccountResponse);
        when(bankAccountHistoryMapper.mapBankAccountHistoryToBankAccountHistoryResponse(
                bankAccountHistoryList.get(0), bankAccountResponse))
                .thenReturn(expectedHistoryResponses.get(0));
        when(bankAccountHistoryMapper.mapBankAccountHistoryToBankAccountHistoryResponse(
                bankAccountHistoryList.get(1), bankAccountResponse))
                .thenReturn(expectedHistoryResponses.get(1));
        when(driverWebClient.getDriver(bankAccountHistoryList.get(0).getBankAccount().getDriverId()))
                .thenReturn(bankUserResponse);

        Page<BankAccountHistory> bankAccountHistoryPage =
                bankAccountHistoryRepository.findAllByBankAccountId(bankAccountId, pageable);
        assertTrue(bankAccountHistoryPage.hasContent());
    }

    @When("Method getBankAccountHistory for bank account with id {long} called with page {int}, size {int}, and sort by {string}")
    public void methodGetBankAccountHistoryForBankAccountWithIdCalledWithPageSizeAndSortBy(long bankAccountId,
                                                                                           int page,
                                                                                           int size,
                                                                                           String sortBy) {
        actualBankAccountHistoryPageResponse =
                bankAccountHistoryService.getBankAccountHistory(bankAccountId, page, size, sortBy);
    }

    @Then("The response should contain a page of bank account history number {int} with size {int}")
    public void theResponseShouldContainAPageOfBankAccountHistoryNumberWithSize(int page, int size) {
        List<BankAccountHistoryResponse> expectedHistoryResponses =
                TestBankAccountHistoryUtil.getBankAccountHistoryResponses();

        BankAccountHistoryPageResponse expected = BankAccountHistoryPageResponse.builder()
                .bankAccountHistoryRecords(expectedHistoryResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();


        assertEquals(expected, actualBankAccountHistoryPageResponse);
    }
}
