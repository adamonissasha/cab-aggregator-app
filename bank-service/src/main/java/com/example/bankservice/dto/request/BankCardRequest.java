package com.example.bankservice.dto.request;

import com.example.bankservice.model.enums.BankUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BankCardRequest {
    private static final String BANK_CARD_NUMBER_FORMAT = "^\\d{4} \\d{4} \\d{4} \\d{4}$";
    private static final String BANK_CARD_EXPIRY_DATE_FORMAT = "^(0[1-9]|1[0-2])/\\d{2}$";
    private static final String BANK_CARD_CVV_FORMAT = "^\\d{3}$";


    @NotBlank(message = "{bank.card.number.required}")
    @Pattern(regexp = BANK_CARD_NUMBER_FORMAT, message = "{bank.card.number.format}")
    private String number;

    @NotBlank(message = "{bank.card.expiry-date.required}")
    @Pattern(regexp = BANK_CARD_EXPIRY_DATE_FORMAT, message = "{bank.card.expiry-date.format}")
    private String expiryDate;

    @NotBlank(message = "{bank.card.cvv.required}")
    @Pattern(regexp = BANK_CARD_CVV_FORMAT, message = "{bank.card.cvv.format}")
    private String cvv;

    private BigDecimal balance;

    @NotBlank(message = "{bank.card.user-id.required}")
    private String bankUserId;

    @NotNull(message = "{bank.card.user.required}")
    private BankUser bankUser;
}