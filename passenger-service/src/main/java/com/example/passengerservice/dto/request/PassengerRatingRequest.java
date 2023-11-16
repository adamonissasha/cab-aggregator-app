package com.example.passengerservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerRatingRequest {
    @NotNull(message = "{passenger-rating.driver-id.not-null}")
    private Long driverId;

    @NotNull(message = "{passenger-rating.rating.required}")
    @Min(value = 1, message = "{passenger-rating.rating.min}")
    @Max(value = 5, message = "{passenger-rating.rating.max}")
    private Integer rating;
}
