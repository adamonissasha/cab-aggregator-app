package com.example.driverservice.dto.request;

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
public class DriverRatingRequest {
    private Long driverId;
    private Long passengerId;
    private Long rideId;
    private Integer rating;
}
