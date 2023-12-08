package com.example.bankservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankAccountRequest {
    @NotBlank(message = "{bank.account.number.required}")
    @Pattern(regexp = "^[A-Za-z\\d]{14}$", message = "{bank.account.number.format}")
    private String number;

    @NotNull(message = "{bank.account.driver-id.required}")
    private Long driverId;
}