package com.example.bankservice.mapper;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.model.BankCard;
import com.example.bankservice.repository.BankCardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BankCardMapper {
    private final ModelMapper modelMapper;
    private final BankCardRepository bankCardRepository;

    public BankCard mapBankCardRequestToBankCard(BankCardRequest bankCardRequest) {
        BankCard bankCard = modelMapper.map(bankCardRequest, BankCard.class);
        bankCard.setId(null);
        bankCard.setIsDefault(!bankCardRepository.existsByBankUserIdAndBankUser(bankCard.getBankUserId(),
                bankCard.getBankUser()));
        return bankCard;
    }

    public BankCardResponse mapBankCardToBankCardResponse(BankCard bankCard, BankUserResponse bankUser) {
        BankCardResponse bankCardResponse = modelMapper.map(bankCard, BankCardResponse.class);
        bankCardResponse.setBankUser(bankUser);
        bankCardResponse.setBankUserRole(bankCard.getBankUser().name());
        return bankCardResponse;
    }
}
