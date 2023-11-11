package com.example.passengerservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerRatingRequest {
    private Long passengerId;
    private Long driverId;
    private Integer rating;
}
