package com.example.bankservice.dto.request;

import com.example.bankservice.model.enums.BankUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankCardRequest {
    @NotBlank(message = "{bank.card.number.required}")
    @Pattern(regexp = "^\\d{4} \\d{4} \\d{4} \\d{4}$", message = "{bank.card.number.format}")
    private String number;

    @NotBlank(message = "{bank.card.expiry-date.required}")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "{bank.card.expiry-date.format}")
    private String expiryDate;

    @NotBlank(message = "{bank.card.cvv.required}")
    @Pattern(regexp = "^\\d{3}$", message = "{bank.card.cvv.format}")
    private String cvv;

    private BigDecimal balance;

    @NotNull(message = "{bank.card.user-id.required}")
    private Long bankUserId;

    @NotBlank(message = "{bank.card.user.required}")
    private BankUser bankUser;
}