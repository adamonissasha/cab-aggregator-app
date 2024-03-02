package com.example.ridesservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RatingRequest {
    @NotNull(message = "{ride.rating.required}")
    @Min(value = 1, message = "{ride.rating.min}")
    @Max(value = 5, message = "{ride.rating.max}")
    private Integer rating;
}
