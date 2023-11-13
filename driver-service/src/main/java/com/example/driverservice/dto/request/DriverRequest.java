package com.example.driverservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DriverRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private Long carId;
}
