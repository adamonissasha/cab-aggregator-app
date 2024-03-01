package com.example.bankservice.mapper;

import com.example.bankservice.dto.request.BankCardRequest;
import com.example.bankservice.dto.response.BankCardResponse;
import com.example.bankservice.dto.response.BankUserResponse;
import com.example.bankservice.model.BankCard;
import com.example.bankservice.repository.BankCardRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BankCardMapper {
    private final ModelMapper modelMapper;
    private final BankCardRepository bankCardRepository;

    @Autowired
    public BankCardMapper(ModelMapper modelMapper,
                          BankCardRepository bankCardRepository) {
        this.modelMapper = modelMapper;
        this.bankCardRepository = bankCardRepository;
        this.modelMapper.addMappings(new PropertyMap<BankCardRequest, BankCard>() {
            @Override
            protected void configure() {
                map().setId(null);
                map().setBankUserId(source.getBankUserId());
            }
        });
    }

    public BankCard mapBankCardRequestToBankCard(BankCardRequest bankCardRequest) {
        BankCard bankCard = modelMapper.map(bankCardRequest, BankCard.class);
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
