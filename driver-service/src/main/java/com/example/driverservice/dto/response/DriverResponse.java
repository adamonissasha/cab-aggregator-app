package com.example.driverservice.dto.response;

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
    private String lastName;
    private String email;
    private String phoneNumber;
    private CarResponse car;
    private String status;
    private double rating;
    private boolean isActive;
}
