package com.example.passengerservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerRatingResponse {
    private Long id;
    private Long passengerId;
    private Long driverId;
    private Integer rating;
}
