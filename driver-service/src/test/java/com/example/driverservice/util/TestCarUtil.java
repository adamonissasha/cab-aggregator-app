package com.example.driverservice.util;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.model.Car;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestCarUtil {
    static Long FIRST_CAR_ID = 1L;
    static Long SECOND_CAR_ID = 2L;
    static String FIRST_CAR_NUMBER = "1234-AA-1";
    static String SECOND_CAR_NUMBER = "1234-BB-2";
    static String FIRST_CAR_COLOR = "red";
    static String SECOND_CAR_COLOR = "black";
    static String FIRST_CAR_MAKE = "BMW";
    static String SECOND_CAR_MAKE = "Audi";
    static int PAGE_NUMBER = 0;
    static int PAGE_SIZE = 2;
    static String SORT_FIELD = "number";

    public static Long getFirstCarId() {
        return FIRST_CAR_ID;
    }

    public static String getFirstCarNumber() {
        return FIRST_CAR_NUMBER;
    }

    public static Car getFirstCar() {
        return Car.builder()
                .id(FIRST_CAR_ID)
                .number(FIRST_CAR_NUMBER)
                .color(FIRST_CAR_COLOR)
                .carMake(FIRST_CAR_MAKE)
                .build();
    }

    public static Car getSecondCar() {
        return Car.builder()
                .id(SECOND_CAR_ID)
                .number(SECOND_CAR_NUMBER)
                .color(SECOND_CAR_COLOR)
                .carMake(SECOND_CAR_MAKE)
                .build();
    }

    public static CarRequest getCarRequest() {
        return CarRequest.builder()
                .number(FIRST_CAR_NUMBER)
                .color(FIRST_CAR_COLOR)
                .carMake(FIRST_CAR_MAKE)
                .build();
    }

    public static CarResponse getCarResponse() {
        return CarResponse.builder()
                .id(FIRST_CAR_ID)
                .number(FIRST_CAR_NUMBER)
                .color(FIRST_CAR_COLOR)
                .carMake(FIRST_CAR_MAKE)
                .build();
    }

    public static int getPageNumber() {
        return PAGE_NUMBER;
    }

    public static int getPageSize() {
        return PAGE_SIZE;
    }

    public static String  getSortField() {
        return SORT_FIELD;
    }
}