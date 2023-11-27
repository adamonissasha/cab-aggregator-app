package com.example.ridesservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateRideRequest {
    @NotNull(message = "{ride.passenger-id.not-null}")
    private Long passengerId;

    @NotBlank(message = "{ride.start-address.required}")
    private String startAddress;

    @NotBlank(message = "{ride.end-address.required}")
    private String endAddress;

    @NotBlank(message = "{ride.payment-method.required}")
    private String paymentMethod;

    private String promoCode;

    @Valid
    private List<StopRequest> stops;
}
