package com.example.bankservice.dto.request;

import com.example.bankservice.model.enums.Operation;
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
public class BankAccountHistoryRequest {
    private Operation operation;
    private BigDecimal sum;
}
