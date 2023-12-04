package com.example.bankservice.repository;

import com.example.bankservice.model.BankCard;
import com.example.bankservice.model.enums.CardHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    Optional<BankCard> findBankCardByNumber(String number);

    boolean existsByCardHolderIdAndCardHolder(Long cardHolderId, CardHolder cardHolder);

    Page<BankCard> findAllByCardHolderIdAndCardHolder(Long cardHolderId, CardHolder cardHolder, Pageable pageable);

    Optional<BankCard> findByCardHolderIdAndCardHolderAndIsDefaultTrue(Long cardHolderId, CardHolder cardHolder);
}