package com.example.ridesservice.dto.response;

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
public class DriverResponse {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private CarResponse car;
    private String status;
}
