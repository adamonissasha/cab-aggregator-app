package com.example.passengerservice.dto.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AllPassengerRatingsResponse {
    private List<PassengerRatingResponse> passengerRatings;
}
