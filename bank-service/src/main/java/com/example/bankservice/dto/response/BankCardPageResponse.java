package com.example.bankservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BankCardPageResponse {
    private List<BankCardResponse> bankCards;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
}
