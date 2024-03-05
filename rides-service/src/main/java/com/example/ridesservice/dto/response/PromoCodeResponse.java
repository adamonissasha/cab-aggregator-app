package com.example.ridesservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PromoCodeResponse {
    private Long id;
    private String code;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer discountPercent;
}
