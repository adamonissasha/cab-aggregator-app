package com.example.driverservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RideDriverResponse {
    private long id;
    private String firstName;
    private String phoneNumber;
    private double rating;
    private CarResponse car;
}