package com.example.bankservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankAccountPageResponse {
    private List<BankAccountResponse> bankAccounts;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
}
