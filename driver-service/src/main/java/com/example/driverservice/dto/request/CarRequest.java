package com.example.driverservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.PropertySource;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@PropertySource("classpath:messages.properties")
public class CarRequest {
    @NotBlank(message = "{car.number.required}")
    private String number;

    @NotBlank(message = "{car.color.required}")
    private String color;

    @NotBlank(message = "{car.make.required}")
    private String carMake;
}
