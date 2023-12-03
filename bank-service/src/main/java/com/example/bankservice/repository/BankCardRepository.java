package com.example.bankservice.repository;

import com.example.bankservice.model.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    Optional<BankCard> findBankCardByNumber(String number);
}