package com.example.passengerservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PassengerRatingRequest {
    private Long driverId;
    private String passengerId;
    private Long rideId;
    private Integer rating;
}
