package com.example.driverservice.dto.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AllDriversResponse {
    private List<DriverRatingResponse> driverRatings;
}
