package com.example.driverservice.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AverageDriverRatingResponse {
    private long passengerId;
    private double averageRating;
}
