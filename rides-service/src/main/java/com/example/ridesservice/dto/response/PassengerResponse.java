package com.example.ridesservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PassengerResponse {
    private String id;
    private String firstName;
    private String phoneNumber;
    private double rating;
}
