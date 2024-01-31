package com.example.bankservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class BankCardResponse {
    private long id;
    private String number;
    private String expiryDate;
    private BigDecimal balance;
    private boolean isDefault;
    private String bankUserRole;
    private BankUserResponse bankUser;
}
