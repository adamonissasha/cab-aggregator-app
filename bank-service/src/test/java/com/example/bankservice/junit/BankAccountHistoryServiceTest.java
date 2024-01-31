package com.example.bankservice.junit;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryPageResponse;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.exception.IncorrectFieldNameException;
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
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankAccountHistoryServiceTest {
    @Mock
    BankAccountHistoryRepository bankAccountHistoryRepository;
    @Mock
    BankAccountHistoryMapper bankAccountHistoryMapper;
    @Mock
    BankAccountMapper bankAccountMapper;
    @Mock
    DriverWebClient driverWebClient;
    @Mock
    FieldValidator fieldValidator;
    @InjectMocks
    BankAccountHistoryServiceImpl bankAccountHistoryService;

    @Test
    void testCreateBankAccountHistoryRecord_WhenBankAccountHistoryIsCreated_ShouldReturnBankAccountHistoryResponse() {
        long bankAccountId = 1L;
        BankAccountHistoryRequest historyRequest = TestBankAccountHistoryUtil.getBankAccountHistoryRequest();
        BankAccountHistory bankAccountHistory = TestBankAccountHistoryUtil.getFirstBankAccountHistory();
        BankUserResponse bankUserResponse = TestBankAccountUtil.getFirstBankUserResponse();
        BankAccountResponse bankAccountResponse = TestBankAccountUtil.getFirstBankAccountResponse();
        BankAccountHistoryResponse expected = TestBankAccountHistoryUtil.getFirstBankAccountHistoryResponse();

        when(bankAccountHistoryMapper.mapBankAccountHistoryRequestToBankAccountHistory(bankAccountId, historyRequest))
                .thenReturn(bankAccountHistory);
        when(bankAccountHistoryRepository.save(bankAccountHistory))
                .thenReturn(bankAccountHistory);
        when(driverWebClient.getDriver(bankAccountId))
                .thenReturn(bankUserResponse);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(any(BankAccount.class), any(BankUserResponse.class)))
                .thenReturn(bankAccountResponse);
        when(bankAccountHistoryMapper.mapBankAccountHistoryToBankAccountHistoryResponse(bankAccountHistory, bankAccountResponse))
                .thenReturn(expected);

        BankAccountHistoryResponse actual = bankAccountHistoryService.createBankAccountHistoryRecord(bankAccountId, historyRequest);

        assertEquals(expected, actual);

        verify(bankAccountHistoryRepository, times(1))
                .save(bankAccountHistory);
        verify(driverWebClient, times(1))
                .getDriver(bankAccountId);
    }

    @Test
    void testGetLastWithdrawalDate_WhenWithdrawalsExist_ShouldReturnLastWithdrawalDate() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        LocalDateTime expected = LocalDateTime.now();
        BankAccountHistory withdrawalHistory = TestBankAccountHistoryUtil.getFirstBankAccountHistory();

        when(bankAccountHistoryRepository.findFirstByBankAccountIdAndOperationOrderByOperationDateTimeDesc(eq(bankAccountId), eq(Operation.WITHDRAWAL)))
                .thenReturn(Optional.of(withdrawalHistory));

        LocalDateTime actual = bankAccountHistoryService.getLastWithdrawalDate(bankAccountId);

        assertThat(actual).isCloseTo(expected, within(10, SECONDS));

        verify(bankAccountHistoryRepository, times(1))
                .findFirstByBankAccountIdAndOperationOrderByOperationDateTimeDesc(eq(bankAccountId), eq(Operation.WITHDRAWAL));
    }

    @Test
    void testGetBankAccountHistory_WhenHistoryExists_ShouldReturnBankAccountHistoryPageResponse() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        int page = TestBankAccountUtil.getPageNumber();
        int size = TestBankAccountUtil.getPageSize();
        String sortBy = TestBankAccountUtil.getCorrectSortField();
        BankUserResponse bankUserResponse = TestBankAccountUtil.getFirstBankUserResponse();
        BankAccountResponse bankAccountResponse = TestBankAccountUtil.getFirstBankAccountResponse();
        List<BankAccountHistory> bankAccountHistoryList = TestBankAccountHistoryUtil.getBankAccountHistoryList();
        List<BankAccountHistoryResponse> expectedHistoryResponses = TestBankAccountHistoryUtil.getBankAccountHistoryResponses();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankAccountHistory> bankAccountHistoryPage = new PageImpl<>(bankAccountHistoryList, pageable, bankAccountHistoryList.size());

        BankAccountHistoryPageResponse expected = BankAccountHistoryPageResponse.builder()
                .bankAccountHistoryRecords(expectedHistoryResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        doNothing()
                .when(fieldValidator)
                .checkSortField(any(), eq(sortBy));
        when(bankAccountHistoryRepository.findAllByBankAccountId(eq(bankAccountId), eq(pageable)))
                .thenReturn(bankAccountHistoryPage);
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

        BankAccountHistoryPageResponse actual = bankAccountHistoryService.getBankAccountHistory(bankAccountId, page, size, sortBy);

        assertEquals(expected, actual);

        verify(bankAccountHistoryRepository, times(1))
                .findAllByBankAccountId(eq(bankAccountId), eq(pageable));
        verify(driverWebClient, times(2))
                .getDriver(bankAccountHistoryList.get(0).getBankAccount().getDriverId());
    }

    @Test
    public void testGetBankAccountHistory_WhenIncorrectField_ShouldThrowIncorrectFieldException() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        int page = TestBankAccountHistoryUtil.getPageNumber();
        int size = TestBankAccountHistoryUtil.getPageSize();
        String sortBy = TestBankAccountHistoryUtil.getIncorrectSortField();

        doThrow(IncorrectFieldNameException.class)
                .when(fieldValidator)
                .checkSortField(eq(BankAccountHistory.class), eq(sortBy));

        assertThrows(IncorrectFieldNameException.class, () -> bankAccountHistoryService.getBankAccountHistory(bankAccountId, page, size, sortBy));

        verify(fieldValidator, times(1))
                .checkSortField(eq(BankAccountHistory.class), eq(sortBy));
        verify(bankAccountHistoryRepository, never())
                .findAll(any(Pageable.class));
    }
}