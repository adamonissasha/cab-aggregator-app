package com.example.ridesservice.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RatingMessage {
    private Long driverId;
    private Long passengerId;
    private Long rideId;
    private Integer rating;
}