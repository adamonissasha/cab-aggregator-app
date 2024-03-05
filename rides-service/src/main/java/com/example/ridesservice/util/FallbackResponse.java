package com.example.ridesservice.util;

import com.example.ridesservice.dto.response.CarResponse;
import com.example.ridesservice.dto.response.DriverResponse;
import com.example.ridesservice.dto.response.PassengerResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FallbackResponse {

    public DriverResponse getDriverFallbackResponse() {
        return DriverResponse.builder()
                .id(-1)
                .firstName("FallbackFirstName")
                .phoneNumber("FallbackPhoneNumber")
                .rating(0)
                .car(getCarFallbackResponse())
                .build();
    }

    public CarResponse getCarFallbackResponse() {
        return CarResponse.builder()
                .id(-1)
                .number("FallbackNumber")
                .carMake("FallbackCarMake")
                .color("FallbackColor")
                .build();
    }

    public PassengerResponse getPassengerResponse() {
        return PassengerResponse.builder()
                .id(-1)
                .firstName("FallbackFirstName")
                .phoneNumber("FallbackPhoneNumber")
                .rating(0)
                .build();
    }
}
