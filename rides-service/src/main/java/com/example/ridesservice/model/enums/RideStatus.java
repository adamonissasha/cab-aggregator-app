package com.example.ridesservice.model.enums;

import lombok.Getter;

@Getter
public enum RideStatus {
    CREATED(""),
    STARTED("The ride with id '%s' has already started"),
    COMPLETED("The ride with id '%s' has already been completed"),
    CANCELED("The ride with id '%s' has already been canceled");

    private final String statusErrorMessage;

    RideStatus(String statusErrorMessage) {
        this.statusErrorMessage = statusErrorMessage;
    }

}