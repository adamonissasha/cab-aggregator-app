package com.example.driverservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CarRequest {
    @NotBlank(message = "{car.number.required}")
    private String number;

    @NotBlank(message = "{car.color.required}")
    private String color;

    @NotBlank(message = "{car.make.required}")
    private String carMake;
}
