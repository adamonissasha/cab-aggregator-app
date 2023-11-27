package com.example.ridesservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StopRequest {
    @NotBlank(message = "{ride.stop.address.required}")
    private String address;

    @Min(value = 1, message = "{ride.stop.number.positive}")
    @NotNull(message = "{ride.stop.number.required}")
    private Integer number;
}
