package com.example.driverservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.PropertySource;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@PropertySource("classpath:messages.properties")
public class DriverRatingRequest {
    @NotNull(message = "{driver-rating.passenger-id.not-null}")
    private Long passengerId;

    @NotNull(message = "{driver-rating.rating.required}")
    @Min(value = 1, message = "{driver-rating.rating.min}")
    @Max(value = 5, message = "{driver-rating.rating.max}")
    private Integer rating;
}
