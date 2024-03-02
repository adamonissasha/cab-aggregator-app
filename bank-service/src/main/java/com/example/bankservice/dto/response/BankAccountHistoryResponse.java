package com.example.bankservice.dto.response;

import com.example.bankservice.model.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankAccountHistoryResponse {
    private long id;
    private BankAccountResponse bankAccount;
    private LocalDateTime operationDateTime;
    private Operation operation;
    private BigDecimal sum;
}
