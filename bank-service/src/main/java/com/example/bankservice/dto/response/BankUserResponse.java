package com.example.bankservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankUserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
