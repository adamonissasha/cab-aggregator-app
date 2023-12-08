package com.example.bankservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankAccountResponse {
    private long id;
    private String number;
    private BigDecimal balance;
    private BankUserResponse driver;
    private Boolean isActive;
}