package com.example.ridesservice.dto.request;

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
    private Long passengerId;
    private String startAddress;
    private String endAddress;
    private String paymentMethod;
    private String promoCode;
    private List<StopRequest> stops;
}
