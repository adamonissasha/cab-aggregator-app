package com.example.bankservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankAccountRequest {
    private static final String ACCOUNT_NUMBER_FORMAT = "^[A-Za-z\\d]{14}$";

    @NotBlank(message = "{bank.account.number.required}")
    @Pattern(regexp = ACCOUNT_NUMBER_FORMAT, message = "{bank.account.number.format}")
    private String number;

    @NotNull(message = "{bank.account.driver-id.required}")
    private Long driverId;
}