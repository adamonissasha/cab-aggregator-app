package com.example.ridesservice.exception;

public class RideReservationNotFoundException extends RuntimeException {
    public RideReservationNotFoundException(String message) {
        super(message);
    }
}
