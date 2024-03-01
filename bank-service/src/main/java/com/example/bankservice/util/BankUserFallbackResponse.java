package com.example.bankservice.util;

import com.example.bankservice.dto.response.BankUserResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BankUserFallbackResponse {

    public BankUserResponse getBankUserFallbackResponse() {
        return BankUserResponse.builder()
                .id("FallbackId")
                .firstName("FallbackFirstName")
                .lastName("FallbackLastName")
                .email("FallbackEmail")
                .phoneNumber("FallbackPhoneNumber")
                .build();
    }
}
