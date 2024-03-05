package com.example.driverservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CarRequest {
    @NotBlank(message = "{car.number.required}")
    private String number;

    @NotBlank(message = "{car.color.required}")
    private String color;

    @NotBlank(message = "{car.make.required}")
    private String carMake;
}
