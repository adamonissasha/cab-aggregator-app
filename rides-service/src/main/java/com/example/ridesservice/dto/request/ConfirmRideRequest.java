package com.example.ridesservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConfirmRideRequest {
    private Long driverId;
    private String driverName;
    private String carNumber;
    private String carMake;
    private String carColor;
}
