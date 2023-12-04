package com.example.bankservice.repository;

import com.example.bankservice.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByNumber(String number);

    Optional<BankAccount> findByDriverId(Long driverId);
}