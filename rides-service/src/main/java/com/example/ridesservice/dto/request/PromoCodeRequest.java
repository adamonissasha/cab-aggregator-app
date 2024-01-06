package com.example.ridesservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PromoCodeRequest {
    @NotBlank(message = "{promo-code.code.required}")
    private String code;

    @NotNull(message = "{promo-code.start-sate.not-null}")
    @Future(message = "{promo-code.start-date.future}")
    private LocalDate startDate;

    @NotNull(message = "{promo-code.end-date.not-null}")
    @Future(message = "{promo-code.end-date.future}")
    private LocalDate endDate;

    @NotNull(message = "{promo-code.discount-percent.not-null}")
    @Min(value = 1, message = "{promo-code.discount-percent.min}")
    @Max(value = 80, message = "{promo-code.discount-percent.max}")
    private Integer discountPercent;
}
