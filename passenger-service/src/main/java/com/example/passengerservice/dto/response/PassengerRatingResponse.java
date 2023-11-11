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
    private long id;
    private long passengerId;
    private long driverId;
    private int rating;
}
