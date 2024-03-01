package com.example.bankservice.repository;

import com.example.bankservice.model.BankCard;
import com.example.bankservice.model.enums.BankUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    Optional<BankCard> findBankCardByNumber(String number);

    boolean existsByBankUserIdAndBankUser(String bankUserId, BankUser bankUser);

    Page<BankCard> findAllByBankUserIdAndBankUser(String bankUserId, BankUser bankUser, Pageable pageable);

    Optional<BankCard> findByBankUserIdAndBankUserAndIsDefaultTrue(String bankUserId, BankUser bankUser);

    void deleteAllByBankUserIdAndBankUser(String bankUserId, BankUser bankUser);
}