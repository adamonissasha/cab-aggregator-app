package com.example.ridesservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PromoCodeRequest {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer discountPercent;
}
