package com.example.bankservice.dto.response;

import com.example.bankservice.model.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class BankAccountHistoryResponse {
    private long id;
    private BankAccountResponse bankAccount;
    private LocalDateTime operationDateTime;
    private Operation operation;
    private BigDecimal sum;
}
