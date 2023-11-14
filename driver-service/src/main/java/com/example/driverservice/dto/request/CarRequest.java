package com.example.driverservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CarRequest {
    @NotBlank(message = "Car number is required")
    private String number;

    @NotBlank(message = "Car color is required")
    private String color;

    @NotBlank(message = "Car make is required")
    private String carMake;
}
