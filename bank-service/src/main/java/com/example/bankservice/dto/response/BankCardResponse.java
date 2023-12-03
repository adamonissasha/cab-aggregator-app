package com.example.bankservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankCardResponse {
    private long id;
    private String number;
    private String expiryDate;
    private double balance;
    private boolean isDefault;
    private String cardHolderRole;
    private CardHolderResponse cardHolder;
}
