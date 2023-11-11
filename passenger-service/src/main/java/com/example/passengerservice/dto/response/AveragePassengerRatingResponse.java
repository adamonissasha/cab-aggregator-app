package com.example.passengerservice.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AveragePassengerRatingResponse {
    private Long passengerId;
    private Double averageRating;
}
