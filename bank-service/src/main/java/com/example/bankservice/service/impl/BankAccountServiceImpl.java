package com.example.bankservice.service.impl;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.request.BankAccountRequest;
import com.example.bankservice.dto.request.RefillRequest;
import com.example.bankservice.dto.request.WithdrawalRequest;
import com.example.bankservice.dto.response.BalanceResponse;
import com.example.bankservice.dto.response.BankAccountPageResponse;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.exception.AccountNumberUniqueException;
import com.example.bankservice.exception.BankAccountNotFoundException;
import com.example.bankservice.exception.DriverBankAccountException;
import com.example.bankservice.exception.WithdrawalException;
import com.example.bankservice.mapper.BankAccountMapper;
import com.example.bankservice.model.BankAccount;
import com.example.bankservice.model.enums.BankUser;
import com.example.bankservice.model.enums.Operation;
import com.example.bankservice.repository.BankAccountRepository;
import com.example.bankservice.service.BankAccountHistoryService;
import com.example.bankservice.service.BankAccountService;
import com.example.bankservice.service.BankCardService;
import com.example.bankservice.util.FieldValidator;
import com.example.bankservice.webClient.DriverWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private static final String BANK_ACCOUNT_NUMBER_EXIST = "Bank account with number '%s' already exist";
    private static final String DRIVER_ALREADY_HAS_ACCOUNT = "Driver with id '%s' already has bank account";
    private static final String BANK_ACCOUNT_NOT_FOUND = "Bank account with id '%s' not found";
    private static final String BANK_ACCOUNT_BY_DRIVER_ID_NOT_FOUND = "Driver with id '%s' bank account not found";
    private static final String WITHDRAWAL_SUM_IS_OUTSIDE = "Withdrawal sum %s isn't included in the range from 30 to 300 BYN";
    private static final String LARGE_WITHDRAWAL_SUM = "Withdrawal sum %s exceeds bank account balance";
    private static final String WITHDRAWAL_DATE_MESSAGE = "Withdrawal from the bank account is allowed once every %s days." +
            "The last withdrawal date - %s. The next available withdrawal date - %s.";
    private static final BigDecimal DRIVER_PERCENT = BigDecimal.valueOf(0.7);
    private static final BigDecimal MIN_WITHDRAWAL_SUM = BigDecimal.valueOf(30);
    private static final BigDecimal MAX_WITHDRAWAL_SUM = BigDecimal.valueOf(300);
    private static final int WITHDRAWAL_LIMIT_DAYS = 7;
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;
    private final DriverWebClient driverWebClient;
    private final BankAccountHistoryService bankAccountHistoryService;
    private final BankCardService bankCardService;
    private final FieldValidator fieldValidator;

    @Override
    @Transactional
    public BankAccountResponse createBankAccount(BankAccountRequest bankAccountRequest) {
        log.info("Creating bank account: {}", bankAccountRequest);

        String accountNumber = bankAccountRequest.getNumber();
        bankAccountRepository.findByNumber(accountNumber)
                .ifPresent(bankAccount -> {
                    throw new AccountNumberUniqueException(String.format(BANK_ACCOUNT_NUMBER_EXIST, accountNumber));
                });

        Long driverId = bankAccountRequest.getDriverId();
        bankAccountRepository.findByDriverId(driverId)
                .ifPresent(bankAccount -> {
                    throw new DriverBankAccountException(String.format(DRIVER_ALREADY_HAS_ACCOUNT, driverId));
                });

        BankAccount newBankAccount = bankAccountMapper.mapBankAccountRequestToBankAccount(bankAccountRequest);
        BankUserResponse bankUserResponse = driverWebClient.getDriver(driverId);
        newBankAccount = bankAccountRepository.save(newBankAccount);
        return bankAccountMapper.mapBankAccountToBankAccountResponse(newBankAccount, bankUserResponse);
    }

    @Override
    public void deleteBankAccount(Long driverId) {
        log.info("Deleting bank account for driver with id: {}", driverId);

        BankAccount bankAccount = bankAccountRepository.findByDriverId(driverId)
                .orElseThrow(() -> new BankAccountNotFoundException(String.format(BANK_ACCOUNT_BY_DRIVER_ID_NOT_FOUND, driverId)));
        bankAccount.setIsActive(false);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public BankAccountResponse getBankAccountById(Long id) {
        log.info("Retrieving bank account with id: {}", id);

        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException(String.format(BANK_ACCOUNT_NOT_FOUND, id)));

        BankUserResponse bankUserResponse = driverWebClient.getDriver(bankAccount.getDriverId());
        return bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccount, bankUserResponse);
    }

    @Override
    public BankAccountPageResponse getAllActiveBankAccounts(int page, int size, String sortBy) {
        log.info("Retrieving all active bank accounts (page {}, size {}, sorted by {})", page, size, sortBy);

        fieldValidator.checkSortField(BankAccount.class, sortBy);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankAccount> bankAccountPage = bankAccountRepository.findAll(pageable);
        List<BankAccountResponse> bankAccountResponses = bankAccountPage.getContent()
                .stream()
                .filter(BankAccount::getIsActive)
                .map(bankAccount -> bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccount,
                        driverWebClient.getDriver(bankAccount.getDriverId())))
                .toList();

        return BankAccountPageResponse.builder()
                .bankAccounts(bankAccountResponses)
                .totalPages(bankAccountPage.getTotalPages())
                .totalElements(bankAccountPage.getTotalElements())
                .currentPage(bankAccountPage.getNumber())
                .pageSize(bankAccountPage.getSize())
                .build();
    }

    @Override
    public BankAccountPageResponse getAllBankAccounts(int page, int size, String sortBy) {
        log.info("Retrieving all bank accounts (page {}, size {}, sorted by {})", page, size, sortBy);

        fieldValidator.checkSortField(BankAccount.class, sortBy);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<BankAccount> bankAccountPage = bankAccountRepository.findAll(pageable);
        List<BankAccountResponse> bankAccountResponses = bankAccountPage.getContent()
                .stream()
                .map(bankAccount -> bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccount,
                        driverWebClient.getDriver(bankAccount.getDriverId())))
                .toList();

        return BankAccountPageResponse.builder()
                .bankAccounts(bankAccountResponses)
                .totalPages(bankAccountPage.getTotalPages())
                .totalElements(bankAccountPage.getTotalElements())
                .currentPage(bankAccountPage.getNumber())
                .pageSize(bankAccountPage.getSize())
                .build();
    }

    @Override
    @Transactional
    public BankAccountResponse refillBankAccount(RefillRequest refillRequest) {
        log.info("Refilling bank account: {}", refillRequest);

        Long driverId = Long.parseLong(refillRequest.getBankUserId());
        BankAccount bankAccount = bankAccountRepository.findByDriverId(driverId)
                .orElseThrow(() -> new BankAccountNotFoundException(String.format(BANK_ACCOUNT_BY_DRIVER_ID_NOT_FOUND, driverId)));

        BigDecimal refillSum = refillRequest.getSum();
        BigDecimal updatedBalance = bankAccount.getBalance()
                .add(DRIVER_PERCENT.multiply(refillSum));
        bankAccount.setBalance(updatedBalance);

        bankAccountHistoryService.createBankAccountHistoryRecord(bankAccount.getId(),
                BankAccountHistoryRequest.builder()
                        .sum(DRIVER_PERCENT.multiply(refillSum))
                        .operation(Operation.REFILL)
                        .build()
        );

        BankUserResponse bankUserResponse = driverWebClient.getDriver(driverId);
        bankAccount = bankAccountRepository.save(bankAccount);
        return bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccount, bankUserResponse);
    }

    @Override
    public BalanceResponse getBankAccountBalance(Long id) {
        log.info("Retrieving balance for bank account with id {}", id);

        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException(String.format(BANK_ACCOUNT_NOT_FOUND, id)));

        return BalanceResponse.builder()
                .balance(bankAccount.getBalance())
                .build();
    }

    @Override
    @Transactional
    public BankAccountResponse withdrawalFromBankAccount(Long id, WithdrawalRequest withdrawalRequest) {
        log.info("Withdrawing money from bank account with id {}: {}", id, withdrawalRequest);

        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException(String.format(BANK_ACCOUNT_NOT_FOUND, id)));

        Long bankUserId = bankAccount.getDriverId();
        BigDecimal withdrawalSum = withdrawalRequest.getSum();
        BigDecimal bankAccountBalance = bankAccount.getBalance();

        checkWithdrawalConditions(id, withdrawalSum, bankAccountBalance);

        bankAccount.setBalance(bankAccountBalance.subtract(withdrawalSum));
        bankAccount = bankAccountRepository.save(bankAccount);

        BankCardResponse defaultBankCard = bankCardService.getDefaultBankCard(bankUserId.toString(), BankUser.DRIVER);

        bankCardService.refillBankCard(defaultBankCard.getId(),
                RefillRequest.builder()
                        .sum(withdrawalSum)
                        .bankUserId(bankUserId.toString())
                        .build());

        bankAccountHistoryService.createBankAccountHistoryRecord(id,
                BankAccountHistoryRequest.builder()
                        .sum(withdrawalSum)
                        .operation(Operation.WITHDRAWAL)
                        .build()
        );

        BankUserResponse bankUserResponse = driverWebClient.getDriver(bankUserId);
        return bankAccountMapper.mapBankAccountToBankAccountResponse(bankAccount, bankUserResponse);
    }

    private void checkWithdrawalConditions(Long id, BigDecimal withdrawalSum, BigDecimal bankAccountBalance) {
        if (withdrawalSum.compareTo(MIN_WITHDRAWAL_SUM) < 0 || withdrawalSum.compareTo(MAX_WITHDRAWAL_SUM) > 0) {
            throw new WithdrawalException(String.format(WITHDRAWAL_SUM_IS_OUTSIDE, withdrawalSum));
        }

        LocalDateTime lastWithdrawalDate = bankAccountHistoryService.getLastWithdrawalDate(id);
        if (lastWithdrawalDate != null) {
            LocalDateTime nextAvailableWithdrawalDate = lastWithdrawalDate.plusDays(WITHDRAWAL_LIMIT_DAYS);
            if (nextAvailableWithdrawalDate.isAfter(LocalDateTime.now())) {
                throw new WithdrawalException(String.format(WITHDRAWAL_DATE_MESSAGE, WITHDRAWAL_LIMIT_DAYS,
                        lastWithdrawalDate, nextAvailableWithdrawalDate));
            }
        }

        if (bankAccountBalance.compareTo(withdrawalSum) < 0) {
            throw new WithdrawalException(String.format(LARGE_WITHDRAWAL_SUM, withdrawalSum));
        }
    }
}