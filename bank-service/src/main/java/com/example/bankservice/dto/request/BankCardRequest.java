package com.example.bankservice.dto.request;

import com.example.bankservice.model.enums.BankUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankCardRequest {
    private String number;
    private String expiryDate;
    private String cvv;
    private BigDecimal balance;
    private Long bankUserId;
    private BankUser bankUser;
}