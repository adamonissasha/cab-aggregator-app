package com.example.bankservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankAccountResponse {
    private long id;
    private String number;
    private BigDecimal balance;
    private BankUserResponse driver;
    private Boolean isActive;
}