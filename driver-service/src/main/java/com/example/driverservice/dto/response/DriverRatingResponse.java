package com.example.driverservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriverRatingResponse {
    private long id;
    private long passengerId;
    private long driverId;
    private int rating;
}
