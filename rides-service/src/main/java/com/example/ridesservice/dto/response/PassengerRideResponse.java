package com.example.ridesservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class PassengerRideResponse {
    private long rideId;
    private String passengerId;
    private String driverName;
    private String driverPhoneNumber;
    private double driverRating;
    private String carNumber;
    private String carMake;
    private String carColor;
    private LocalDateTime creationDateTime;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status;
    private BigDecimal price;
    private String promoCode;
    private String paymentMethod;
    private String startAddress;
    private String endAddress;
    private List<StopResponse> stops;
}
