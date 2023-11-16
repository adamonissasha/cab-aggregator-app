package com.example.passengerservice.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AveragePassengerRatingResponse {
    private long passengerId;
    private double averageRating;
}
