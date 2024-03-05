package com.example.passengerservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AveragePassengerRatingResponse {
    private String passengerId;
    private double averageRating;
}
