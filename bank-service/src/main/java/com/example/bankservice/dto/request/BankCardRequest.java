package com.example.bankservice.dto.request;

import com.example.bankservice.model.enums.CardHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankCardRequest {
    private String number;
    private String expiryDate;
    private String cvv;
    private double balance;
    private long cardHolderId;
    private CardHolder cardHolder;
}