package com.example.driverservice.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CarResponse {
    private long id;
    private String number;
    private String color;
    private String carMake;
}
