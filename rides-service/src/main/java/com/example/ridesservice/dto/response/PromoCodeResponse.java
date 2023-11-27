package com.example.ridesservice.dto.response;

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
public class PromoCodeResponse {
    private Long id;
    private String code;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer discountPercent;
}
