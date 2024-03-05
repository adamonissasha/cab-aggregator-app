package com.example.ridesservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriverResponse {
    private long id;
    private String firstName;
    private String phoneNumber;
    private double rating;
    private CarResponse car;
}