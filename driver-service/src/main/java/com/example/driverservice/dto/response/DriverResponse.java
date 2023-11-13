package com.example.driverservice.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DriverResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Long carId;
}
