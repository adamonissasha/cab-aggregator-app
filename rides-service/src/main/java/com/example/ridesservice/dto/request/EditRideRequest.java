package com.example.ridesservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EditRideRequest {
    @NotBlank(message = "{ride.start-address.required}")
    private String startAddress;

    @NotBlank(message = "{ride.end-address.required}")
    private String endAddress;

    @NotBlank(message = "{ride.payment-method.required}")
    private String paymentMethod;

    @Valid
    private List<StopRequest> stops;
}
