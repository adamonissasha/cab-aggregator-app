package com.example.bankservice.mapper;

import com.example.bankservice.dto.request.BankAccountRequest;
import com.example.bankservice.dto.response.BankAccountResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.model.BankAccount;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BankAccountMapper {
    private final ModelMapper modelMapper;

    public BankAccount mapBankAccountRequestToBankAccount(BankAccountRequest bankAccountRequest) {
        BankAccount bankAccount = modelMapper.map(bankAccountRequest, BankAccount.class);
        bankAccount.setId(null);
        bankAccount.setIsActive(true);
        bankAccount.setBalance(BigDecimal.ZERO);
        return bankAccount;
    }

    public BankAccountResponse mapBankAccountToBankAccountResponse(BankAccount bankAccount, BankUserResponse driverResponse) {
        BankAccountResponse bankAccountResponse = modelMapper.map(bankAccount, BankAccountResponse.class);
        bankAccountResponse.setDriver(driverResponse);
        return bankAccountResponse;
    }
}
