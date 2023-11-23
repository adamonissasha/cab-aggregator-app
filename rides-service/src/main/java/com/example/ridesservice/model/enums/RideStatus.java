package com.example.ridesservice.model.enums;

import lombok.Getter;

@Getter
public enum RideStatus {
    CREATED(""),
    CONFIRMED("The ride has already been confirmed!"),
    STARTED("The ride has already started!"),
    COMPLETED("The ride has already been completed!"),
    CANCELED("The ride has already been canceled!");

    private final String statusErrorMessage;

    RideStatus(String statusErrorMessage) {
        this.statusErrorMessage = statusErrorMessage;
    }

}