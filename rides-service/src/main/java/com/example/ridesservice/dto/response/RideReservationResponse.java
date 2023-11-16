package com.example.ridesservice.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RideReservationResponse {
    private long id;
    private long passengerId;
    private LocalDate dateOfCreation;
    private LocalTime timeOfCreation;
    private String status;
    private double price;
    private String startAddress;
    private String endAddress;
    private String paymentMethod;
    private String promoCode;
    private List<StopResponse> stops;
}
