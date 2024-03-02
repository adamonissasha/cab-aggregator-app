package com.example.bankservice.dto.request;

import com.example.bankservice.model.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankAccountHistoryRequest {
    private Operation operation;
    private BigDecimal sum;
}
