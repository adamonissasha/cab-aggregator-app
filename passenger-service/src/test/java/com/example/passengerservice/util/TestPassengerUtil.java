package com.example.passengerservice.util;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.model.Passenger;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestPassengerUtil {
    static String CAR_NOT_FOUND_MESSAGE = "Car with id '%s' not found";
    static Long FIRST_PASSENGER_ID = 1L;
    static Long SECOND_PASSENGER_ID = 2L;
    static String FIRST_PASSENGER_FIRST_NAME = "Ivan";
    static String SECOND_PASSENGER_FIRST_NAME = "Alex";
    static String FIRST_PASSENGER_LAST_NAME = "Ivanov";
    static String SECOND_PASSENGER_LAST_NAME = "Alexandrov";
    static String FIRST_PASSENGER_EMAIL = "ivan@gmail.com";
    static String SECOND_PASSENGER_EMAIL = "alex@gmail.com";
    static String FIRST_PASSENGER_PHONE_NUMBER = "+375291234567";
    static String SECOND_PASSENGER_PHONE_NUMBER = "+375297654321";
    static String FIRST_PASSENGER_PASSWORD = "123";
    static String SECOND_PASSENGER_PASSWORD = "321";
    static Long PASSENGER_CAR_ID = 1L;
    static Double PASSENGER_RATING = 4.5;
    static int PAGE_NUMBER = 0;
    static int PAGE_SIZE = 2;
    static String SORT_FIELD = "number";

    public static Long getFirstPassengerId() {
        return FIRST_PASSENGER_ID;
    }

    public static String getFirstPassengerPhoneNumber() {
        return FIRST_PASSENGER_PHONE_NUMBER;
    }

    public static Passenger getFirstPassenger() {
        return Passenger.builder()
                .id(FIRST_PASSENGER_ID)
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(FIRST_PASSENGER_PHONE_NUMBER)
                .password(FIRST_PASSENGER_PASSWORD)
                .build();
    }

    public static Passenger getSecondPassenger() {
        return Passenger.builder()
                .id(SECOND_PASSENGER_ID)
                .firstName(SECOND_PASSENGER_FIRST_NAME)
                .lastName(SECOND_PASSENGER_LAST_NAME)
                .email(SECOND_PASSENGER_EMAIL)
                .phoneNumber(SECOND_PASSENGER_PHONE_NUMBER)
                .password(SECOND_PASSENGER_PASSWORD)
                .build();
    }

    public static PassengerRequest getPassengerRequest() {
        return PassengerRequest.builder()
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(FIRST_PASSENGER_PHONE_NUMBER)
                .password(FIRST_PASSENGER_PASSWORD)
                .build();
    }

    public static PassengerResponse getPassengerResponse() {
        return PassengerResponse.builder()
                .id(FIRST_PASSENGER_ID)
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(FIRST_PASSENGER_PHONE_NUMBER)
                .rating(PASSENGER_RATING)
                .build();
    }

    public static PassengerResponse getSecondPassengerResponse() {
        return PassengerResponse.builder()
                .id(SECOND_PASSENGER_ID)
                .firstName(SECOND_PASSENGER_FIRST_NAME)
                .lastName(SECOND_PASSENGER_LAST_NAME)
                .email(SECOND_PASSENGER_EMAIL)
                .phoneNumber(SECOND_PASSENGER_PHONE_NUMBER)
                .rating(PASSENGER_RATING)
                .build();
    }

    public static AveragePassengerRatingResponse getPassengerRating() {
        return AveragePassengerRatingResponse.builder()
                .averageRating(PASSENGER_RATING)
                .build();
    }

    public static int getPageNumber() {
        return PAGE_NUMBER;
    }

    public static int getPageSize() {
        return PAGE_SIZE;
    }

    public static String getSortField() {
        return SORT_FIELD;
    }
}