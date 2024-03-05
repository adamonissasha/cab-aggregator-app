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
public class BankCardPageResponse {
    private List<BankCardResponse> bankCards;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
}
