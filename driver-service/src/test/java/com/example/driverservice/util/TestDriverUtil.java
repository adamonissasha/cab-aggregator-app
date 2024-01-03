package com.example.driverservice.util;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.model.Driver;
import com.example.driverservice.model.enums.Status;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestDriverUtil {
    static String CAR_NOT_FOUND_MESSAGE = "Car with id '%s' not found";
    static Long FIRST_DRIVER_ID = 1L;
    static Long SECOND_DRIVER_ID = 2L;
    static String FIRST_DRIVER_FIRST_NAME = "Ivan";
    static String SECOND_DRIVER_FIRST_NAME = "Alex";
    static String FIRST_DRIVER_LAST_NAME = "Ivanov";
    static String SECOND_DRIVER_LAST_NAME = "Alexandrov";
    static String FIRST_DRIVER_EMAIL = "ivan@gmail.com";
    static String SECOND_DRIVER_EMAIL = "alex@gmail.com";
    static String FIRST_DRIVER_PHONE_NUMBER = "+375291234567";
    static String SECOND_DRIVER_PHONE_NUMBER = "+375297654321";
    static String FIRST_DRIVER_PASSWORD = "123";
    static String SECOND_DRIVER_PASSWORD = "321";
    static Long DRIVER_CAR_ID = 1L;
    static Double DRIVER_RATING = 4.5;
    static int PAGE_NUMBER = 0;
    static int PAGE_SIZE = 2;
    static String SORT_FIELD = "number";

    public static Long getFirstDriverId() {
        return FIRST_DRIVER_ID;
    }

    public static String getFirstDriverPhoneNumber() {
        return FIRST_DRIVER_PHONE_NUMBER;
    }

    public static Long getDriverCarId() {
        return DRIVER_CAR_ID;
    }

    public static String getCarNotFoundMessage() {
        return CAR_NOT_FOUND_MESSAGE;
    }

    public static Driver getFirstDriver() {
        return Driver.builder()
                .id(FIRST_DRIVER_ID)
                .firstName(FIRST_DRIVER_FIRST_NAME)
                .lastName(FIRST_DRIVER_LAST_NAME)
                .email(FIRST_DRIVER_EMAIL)
                .phoneNumber(FIRST_DRIVER_PHONE_NUMBER)
                .password(FIRST_DRIVER_PASSWORD)
                .status(Status.FREE)
                .car(TestCarUtil.getFirstCar())
                .build();
    }

    public static Driver getSecondDriver() {
        return Driver.builder()
                .id(SECOND_DRIVER_ID)
                .firstName(SECOND_DRIVER_FIRST_NAME)
                .lastName(SECOND_DRIVER_LAST_NAME)
                .email(SECOND_DRIVER_EMAIL)
                .phoneNumber(SECOND_DRIVER_PHONE_NUMBER)
                .password(SECOND_DRIVER_PASSWORD)
                .status(Status.FREE)
                .car(TestCarUtil.getSecondCar())
                .build();
    }

    public static DriverRequest getDriverRequest() {
        return DriverRequest.builder()
                .firstName(FIRST_DRIVER_FIRST_NAME)
                .lastName(FIRST_DRIVER_LAST_NAME)
                .email(FIRST_DRIVER_EMAIL)
                .phoneNumber(FIRST_DRIVER_PHONE_NUMBER)
                .password(FIRST_DRIVER_PASSWORD)
                .carId(DRIVER_CAR_ID)
                .build();
    }

    public static DriverResponse getDriverResponse() {
        return DriverResponse.builder()
                .id(FIRST_DRIVER_ID)
                .firstName(FIRST_DRIVER_FIRST_NAME)
                .lastName(FIRST_DRIVER_LAST_NAME)
                .email(FIRST_DRIVER_EMAIL)
                .phoneNumber(FIRST_DRIVER_PHONE_NUMBER)
                .car(TestCarUtil.getCarResponse())
                .status(Status.FREE.name())
                .rating(DRIVER_RATING)
                .build();
    }

    public static DriverResponse getSecondDriverResponse() {
        return DriverResponse.builder()
                .id(SECOND_DRIVER_ID)
                .firstName(SECOND_DRIVER_FIRST_NAME)
                .lastName(SECOND_DRIVER_LAST_NAME)
                .email(SECOND_DRIVER_EMAIL)
                .phoneNumber(SECOND_DRIVER_PHONE_NUMBER)
                .car(TestCarUtil.getCarResponse())
                .status(Status.FREE.name())
                .rating(DRIVER_RATING)
                .build();
    }

    public static AverageDriverRatingResponse getDriverRating() {
        return AverageDriverRatingResponse.builder()
                .averageRating(DRIVER_RATING)
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