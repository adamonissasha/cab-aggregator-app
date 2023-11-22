package com.example.ridesservice.model.enums;

import lombok.Getter;

@Getter
public enum RideStatus {
    CREATED(""),
    CONFIRMED("The ride has already been confirmed!"),
    STARTED("The ride has already started!"),
    COMPLETED("The ride has already been completed!"),
    CANCELED("The ride has already been canceled!");

    private final String message;

    RideStatus(String message) {
        this.message = message;
    }

}