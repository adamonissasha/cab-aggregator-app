package com.example.ridesservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConfirmRideRequest {
    @NotNull(message = "{ride.driver.id.not-null}")
    private Long driverId;

    @NotBlank(message = "{ride.driver.name.required}")
    private String driverName;

    @NotBlank(message = "{ride.car.number.required}")
    private String carNumber;

    @NotBlank(message = "{ride.car.make.required}")
    private String carMake;

    @NotBlank(message = "{ride.car.color.required}")
    private String carColor;
}
