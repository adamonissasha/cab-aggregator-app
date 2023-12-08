package com.example.bankservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankCardResponse {
    private long id;
    private String number;
    private String expiryDate;
    private BigDecimal balance;
    private boolean isDefault;
    private String bankUserRole;
    private BankUserResponse bankUser;
}
