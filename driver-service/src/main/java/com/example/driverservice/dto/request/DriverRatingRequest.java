package com.example.driverservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriverRatingRequest {
    private Long driverId;
    private Long passengerId;
    private Long rideId;
    private Integer rating;
}
