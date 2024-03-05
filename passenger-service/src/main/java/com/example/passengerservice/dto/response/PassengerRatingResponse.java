package com.example.passengerservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PassengerRatingResponse {
    private String id;
    private String passengerId;
    private long driverId;
    private int rating;
}
