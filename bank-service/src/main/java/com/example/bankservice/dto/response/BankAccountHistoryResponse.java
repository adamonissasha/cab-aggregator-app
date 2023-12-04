package com.example.bankservice.dto.response;

import com.example.bankservice.model.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankAccountHistoryResponse {
    private long id;
    private BankAccountResponse bankAccount;
    private LocalDateTime operationDateTime;
    private Operation operation;
    private BigDecimal sum;
}
