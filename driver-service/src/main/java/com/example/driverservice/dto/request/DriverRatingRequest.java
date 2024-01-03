package com.example.driverservice.dto.request;

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
public class DriverRatingRequest {
    private Long driverId;
    private Long passengerId;
    private Long rideId;
    private Integer rating;
}
