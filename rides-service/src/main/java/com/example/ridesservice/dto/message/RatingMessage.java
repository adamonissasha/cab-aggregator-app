package com.example.ridesservice.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RatingMessage {
    private Long driverId;
    private String passengerId;
    private Long rideId;
    private Integer rating;
}