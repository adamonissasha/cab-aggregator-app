package com.example.bankservice.service;

import com.example.bankservice.dto.request.BankAccountRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankAccountPageResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.exception.BankAccountNotFoundException;
import com.example.bankservice.exception.CardNumberUniqueException;
import com.example.bankservice.exception.DriverBankAccountException;
import com.example.bankservice.mapper.BankAccountMapper;
import com.example.bankservice.model.BankAccount;
import com.example.bankservice.repository.BankAccountRepository;
import com.example.bankservice.service.impl.BankAccountServiceImpl;
import com.example.bankservice.util.FieldValidator;
import com.example.bankservice.util.TestBankAccountUtil;
import com.example.bankservice.util.TestBankCardUtil;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankAccountServiceTest {
    @Mock
    BankAccountRepository bankAccountRepository;
    @Mock
    BankAccountMapper bankAccountMapper;
    @Mock
    DriverWebClient driverWebClient;
    @Mock
    FieldValidator fieldValidator;
    @Mock
    BankCardService bankCardService;
    @Mock
    BankAccountHistoryService bankAccountHistoryService;
    @InjectMocks
    BankAccountServiceImpl bankAccountService;

    @Test
    void testCreateBankAccount_WhenAccountNumberUnique_ShouldCreateBankAccount() {
        BankAccountRequest bankAccountRequest = TestBankAccountUtil.getBankAccountRequest();
        Long driverId = bankAccountRequest.getDriverId();
        BankAccount newBankAccount = TestBankAccountUtil.getFirstBankAccount();
        BankUserResponse bankUserResponse = TestBankAccountUtil.getBankUserResponse();
        BankAccountResponse expected = TestBankAccountUtil.getFirstBankAccountResponse();

        when(bankAccountRepository.findByDriverId(driverId))
                .thenReturn(Optional.empty());
        when(bankAccountRepository.findByNumber(bankAccountRequest.getNumber()))
                .thenReturn(Optional.empty());
        when(bankAccountMapper.mapBankAccountRequestToBankAccount(bankAccountRequest))
                .thenReturn(newBankAccount);
        when(driverWebClient.getDriver(driverId))
                .thenReturn(bankUserResponse);
        when(bankAccountRepository.save(newBankAccount))
                .thenReturn(newBankAccount);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(newBankAccount, bankUserResponse))
                .thenReturn(expected);

        BankAccountResponse actual = bankAccountService.createBankAccount(bankAccountRequest);

        assertEquals(expected, actual);

        verify(bankAccountRepository, times(1))
                .findByDriverId(driverId);
        verify(bankAccountRepository, times(1))
                .findByNumber(bankAccountRequest.getNumber());
        verify(driverWebClient, times(1))
                .getDriver(driverId);
        verify(bankAccountRepository, times(1))
                .save(newBankAccount);
    }

    @Test
    void testCreateBankAccount_WhenDriverAccountAlreadyExists_ShouldThrowDriverBankAccountException() {
        BankAccountRequest bankAccountRequest = TestBankAccountUtil.getBankAccountRequest();
        Long driverId = bankAccountRequest.getDriverId();
        BankAccount existingBankAccount = TestBankAccountUtil.getFirstBankAccount();

        when(bankAccountRepository.findByDriverId(driverId))
                .thenReturn(Optional.of(existingBankAccount));

        assertThrows(DriverBankAccountException.class, () -> bankAccountService.createBankAccount(bankAccountRequest));

        verify(bankAccountRepository, times(1))
                .findByDriverId(driverId);
    }

    @Test
    void testCreateBankAccount_WhenAccountNumberExists_ShouldThrowCardNumberUniqueException() {
        BankAccountRequest bankAccountRequest = TestBankAccountUtil.getBankAccountRequest();
        String accountNumber = bankAccountRequest.getNumber();
        BankAccount existingBankAccount = TestBankAccountUtil.getFirstBankAccount();
        Long driverId = bankAccountRequest.getDriverId();

        when(bankAccountRepository.findByDriverId(driverId))
                .thenReturn(Optional.empty());
        when(bankAccountRepository.findByNumber(accountNumber))
                .thenReturn(Optional.of(existingBankAccount));

        assertThrows(CardNumberUniqueException.class, () -> bankAccountService.createBankAccount(bankAccountRequest));

        verify(bankAccountRepository, times(1))
                .findByDriverId(driverId);
        verify(bankAccountRepository, times(1))
                .findByNumber(bankAccountRequest.getNumber());
    }

    @Test
    void testDeleteBankAccount_WhenBankAccountExists_ShouldSetInactive() {
        Long driverId = 1L;
        BankAccount existingBankAccount = TestBankAccountUtil.getFirstBankAccount();

        when(bankAccountRepository.findByDriverId(driverId))
                .thenReturn(Optional.of(existingBankAccount));

        bankAccountService.deleteBankAccount(driverId);

        assertFalse(existingBankAccount.getIsActive());

        verify(bankAccountRepository, times(1))
                .findByDriverId(driverId);
        verify(bankAccountRepository, times(1))
                .save(existingBankAccount);
    }

    @Test
    void testDeleteBankAccount_WhenBankAccountDoesNotExist_ShouldThrowBankAccountNotFoundException() {
        Long driverId = 1L;

        when(bankAccountRepository.findByDriverId(driverId))
                .thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.deleteBankAccount(driverId));
    }

    @Test
    void testGetBankAccountById_WhenBankAccountExists_ShouldReturnBankAccountResponse() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        BankAccount existingBankAccount = TestBankAccountUtil.getFirstBankAccount();
        BankUserResponse bankUserResponse = TestBankAccountUtil.getBankUserResponse();
        BankAccountResponse expected = TestBankAccountUtil.getFirstBankAccountResponse();

        when(bankAccountRepository.findById(bankAccountId))
                .thenReturn(Optional.of(existingBankAccount));
        when(driverWebClient.getDriver(existingBankAccount.getDriverId()))
                .thenReturn(bankUserResponse);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(existingBankAccount, bankUserResponse))
                .thenReturn(expected);

        BankAccountResponse actual = bankAccountService.getBankAccountById(bankAccountId);

        assertEquals(expected, actual);
    }

    @Test
    void testGetBankAccountById_WhenBankAccountDoesNotExist_ShouldThrowBankAccountNotFoundException() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        when(bankAccountRepository.findById(bankAccountId))
                .thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.getBankAccountById(bankAccountId));
    }

    @Test
    void testGetAllActiveBankAccounts_ShouldReturnBankAccountPageResponse() {
        int page = TestBankAccountUtil.getPageNumber();
        int size = TestBankAccountUtil.getPageSize();
        String sortBy = TestBankAccountUtil.getSortField();
        BankUserResponse bankUserResponse = TestBankAccountUtil.getBankUserResponse();
        List<BankAccount> activeBankAccounts = TestBankAccountUtil.getBankAccounts();
        List<BankAccountResponse> expectedResponses = TestBankAccountUtil.getBankAccountResponses();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankAccount> bankAccountPage = new PageImpl<>(activeBankAccounts, pageable, activeBankAccounts.size());

        BankAccountPageResponse expected = BankAccountPageResponse.builder()
                .bankAccounts(expectedResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        doNothing()
                .when(fieldValidator)
                .checkSortField(any(), eq(sortBy));
        when(bankAccountRepository.findAll(pageable))
                .thenReturn(bankAccountPage);
        when(driverWebClient.getDriver(activeBankAccounts.get(0).getDriverId()))
                .thenReturn(bankUserResponse);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(activeBankAccounts.get(0), bankUserResponse))
                .thenReturn(expectedResponses.get(0));
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(activeBankAccounts.get(1), bankUserResponse))
                .thenReturn(expectedResponses.get(1));

        BankAccountPageResponse actual = bankAccountService.getAllActiveBankAccounts(page, size, sortBy);

        assertEquals(expected, actual);

        verify(bankAccountRepository, times(1))
                .findAll(pageable);
        verify(driverWebClient, times(2))
                .getDriver(activeBankAccounts.get(0).getDriverId());
    }

    @Test
    void testGetAllBankAccounts_ShouldReturnBankAccountPageResponse() {
        int page = TestBankAccountUtil.getPageNumber();
        int size = TestBankAccountUtil.getPageSize();
        String sortBy = TestBankAccountUtil.getSortField();
        BankUserResponse bankUserResponse = TestBankAccountUtil.getBankUserResponse();
        List<BankAccount> bankAccounts = TestBankAccountUtil.getBankAccounts();
        List<BankAccountResponse> expectedResponses = TestBankAccountUtil.getBankAccountResponses();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankAccount> bankAccountPage = new PageImpl<>(bankAccounts, pageable, bankAccounts.size());

        BankAccountPageResponse expected = BankAccountPageResponse.builder()
                .bankAccounts(expectedResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        doNothing()
                .when(fieldValidator)
                .checkSortField(any(), eq(sortBy));
        when(bankAccountRepository.findAll(pageable))
                .thenReturn(bankAccountPage);
        when(driverWebClient.getDriver(bankAccounts.get(0).getDriverId()))
                .thenReturn(bankUserResponse);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccounts.get(0), bankUserResponse))
                .thenReturn(expectedResponses.get(0));
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccounts.get(1), bankUserResponse))
                .thenReturn(expectedResponses.get(1));

        BankAccountPageResponse actual = bankAccountService.getAllBankAccounts(page, size, sortBy);

        assertEquals(expected, actual);

        verify(bankAccountRepository, times(1))
                .findAll(pageable);
        verify(driverWebClient, times(2))
                .getDriver(bankAccounts.get(0).getDriverId());
    }

    @Test
    void testRefillBankAccount_WhenBankAccountExists_ShouldRefillBankAccount() {
        RefillRequest refillRequest = TestBankAccountUtil.getRefillRequest();
        BankAccount bankAccount = TestBankAccountUtil.getFirstBankAccount();
        BankUserResponse bankUserResponse = TestBankAccountUtil.getBankUserResponse();
        BankAccountResponse expected = TestBankAccountUtil.getFirstBankAccountResponse();

        when(bankAccountRepository.findByDriverId(refillRequest.getBankUserId()))
                .thenReturn(Optional.of(bankAccount));
        when(driverWebClient.getDriver(refillRequest.getBankUserId()))
                .thenReturn(bankUserResponse);
        when(bankAccountRepository.save(bankAccount))
                .thenReturn(bankAccount);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccount, bankUserResponse))
                .thenReturn(expected);

        BankAccountResponse actual = bankAccountService.refillBankAccount(refillRequest);

        assertEquals(expected, actual);

        verify(bankAccountRepository, times(1))
                .save(bankAccount);
        verify(bankAccountRepository, times(1))
                .findByDriverId(refillRequest.getBankUserId());
        verify(driverWebClient, times(1))
                .getDriver(refillRequest.getBankUserId());
        verify(bankAccountHistoryService, times(1))
                .createBankAccountHistoryRecord(eq(bankAccount.getId()), any());
    }

    @Test
    void testRefillBankAccount_WhenBankAccountNotFound_ShouldThrowBankAccountNotFoundException() {
        RefillRequest refillRequest = TestBankAccountUtil.getRefillRequest();

        when(bankAccountRepository.findByDriverId(refillRequest.getBankUserId()))
                .thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.refillBankAccount(refillRequest));

        verify(bankAccountRepository, times(1))
                .findByDriverId(refillRequest.getBankUserId());
    }

    @Test
    void testGetBankAccountBalance_WhenBankAccountExists_ShouldReturnBalanceResponse() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        BalanceResponse expected = TestBankAccountUtil.getBalanceResponse();
        BankAccount bankAccount = TestBankAccountUtil.getFirstBankAccount();

        when(bankAccountRepository.findById(bankAccountId))
                .thenReturn(Optional.of(bankAccount));

        BalanceResponse actual = bankAccountService.getBankAccountBalance(bankAccountId);

        assertEquals(expected, actual);

        verify(bankAccountRepository, times(1))
                .findById(bankAccountId);
    }

    @Test
    void testGetBankAccountBalance_WhenBankAccountNotFound_ShouldThrowBankAccountNotFoundException() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();

        when(bankAccountRepository.findById(bankAccountId))
                .thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.getBankAccountBalance(bankAccountId));

        verify(bankAccountRepository, times(1))
                .findById(bankAccountId);
    }

    @Test
    void testWithdrawalFromBankAccount_WhenBankAccountExistsAndConditionsAreMet_ShouldWithdrawFromAccount() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        WithdrawalRequest withdrawalRequest = TestBankAccountUtil.getWithdrawalRequest();
        BankAccount bankAccount = TestBankAccountUtil.getFirstBankAccount();
        BankUserResponse bankUserResponse = TestBankAccountUtil.getBankUserResponse();
        BankAccountResponse expected = TestBankAccountUtil.getFirstBankAccountResponse();

        when(bankAccountRepository.findById(bankAccountId))
                .thenReturn(Optional.of(bankAccount));
        when(driverWebClient.getDriver(bankAccount.getDriverId()))
                .thenReturn(bankUserResponse);
        when(bankCardService.refillBankCard(eq(bankAccountId), any()))
                .thenReturn(TestBankCardUtil.getFirstBankCardResponse());
        when(bankAccountRepository.save(bankAccount))
                .thenReturn(bankAccount);
        when(bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccount, bankUserResponse))
                .thenReturn(expected);

        BankAccountResponse actual = bankAccountService.withdrawalFromBankAccount(bankAccountId, withdrawalRequest);

        assertEquals(expected, actual);

        verify(bankAccountRepository, times(1))
                .save(bankAccount);
        verify(bankCardService, times(1))
                .refillBankCard(eq(bankAccountId), any());
        verify(bankAccountHistoryService, times(1))
                .createBankAccountHistoryRecord(eq(bankAccountId), any());
    }

    @Test
    void testWithdrawalFromBankAccount_WhenBankAccountNotFound_ShouldThrowBankAccountNotFoundException() {
        Long bankAccountId = TestBankAccountUtil.getBankAccountId();
        WithdrawalRequest withdrawalRequest = TestBankAccountUtil.getWithdrawalRequest();

        when(bankAccountRepository.findById(bankAccountId))
                .thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class,
                () -> bankAccountService.withdrawalFromBankAccount(bankAccountId, withdrawalRequest));
    }
}