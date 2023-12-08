package com.example.bankservice.controller;

import com.example.bankservice.dto.request.BankAccountHistoryRequest;
import com.example.bankservice.dto.response.BankAccountHistoryPageResponse;
import com.example.bankservice.dto.response.BankAccountHistoryResponse;
import com.example.bankservice.service.BankAccountHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bank/account/{id}/history")
public class BankAccountHistoryController {
    private final BankAccountHistoryService bankAccountHistoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountHistoryResponse createBankAccountHistoryRecord(@PathVariable("id") Long id,
                                                                     @RequestBody BankAccountHistoryRequest bankAccountHistoryRequest) {
        return bankAccountHistoryService.createBankAccountHistoryRecord(id, bankAccountHistoryRequest);
    }

    @GetMapping
    public BankAccountHistoryPageResponse getBankAccountHistory(@PathVariable("id") Long id,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "id") String sortBy) {
        return bankAccountHistoryService.getBankAccountHistory(id, page, size, sortBy);
    }
}