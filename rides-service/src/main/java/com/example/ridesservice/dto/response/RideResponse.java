package com.example.ridesservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RideResponse {
    private long rideId;
    private long passengerId;
    private long driverId;
    private LocalDateTime creationDateTime;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status;
    private double price;
    private String promoCode;
    private String paymentMethod;
    private String startAddress;
    private String endAddress;
    private List<StopResponse> stops;
}
