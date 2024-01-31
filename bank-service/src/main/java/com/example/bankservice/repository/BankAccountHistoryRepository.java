package com.example.bankservice.repository;

import com.example.bankservice.model.BankAccountHistory;
import com.example.bankservice.model.enums.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountHistoryRepository extends JpaRepository<BankAccountHistory, Long> {
    Page<BankAccountHistory> findAllByBankAccountId(Long id, Pageable pageable);

    Optional<BankAccountHistory> findFirstByBankAccountIdAndOperationOrderByOperationDateTimeDesc(Long id, Operation operation);

    void deleteAllByBankAccountId(Long id);
}