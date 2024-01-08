package com.example.driverservice.util;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarPageResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.dto.response.ValidationErrorResponse;
import com.example.driverservice.model.Car;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestCarUtil {
    static Long NEW_CAR_ID = 100L;
    static Long FIRST_CAR_ID = 98L;
    static Long SECOND_CAR_ID = 99L;
    static Long INVALID_CAR_ID = 199L;
    static String FIRST_CAR_NUMBER = "1234-AA-1";
    static String SECOND_CAR_NUMBER = "1234-BB-2";
    static String NEW_CAR_NUMBER = "1235-BB-2";
    static String EXISTING_CAR_NUMBER = "1223-NM-4";
    static String FIRST_CAR_COLOR = "red";
    static String SECOND_CAR_COLOR = "black";
    static String NEW_CAR_COLOR = "white";
    static String FIRST_CAR_MAKE = "BMW";
    static String SECOND_CAR_MAKE = "Audi";
    static String NEW_CAR_MAKE = "Volvo";
    static int PAGE_NUMBER = 1;
    static int PAGE_SIZE = 2;
    static String SORT_FIELD = "id";
    static String CAR_NOT_FOUND = "Car with id '%s' not found";
    static String CAR_NUMBER_EXIST = "Car with number '%s' already exist";
    static String CAR_NUMBER_REQUIRED = "Car number is required";
    static String CAR_COLOR_REQUIRED = "Car color is required";
    static String CAR_MAKE_REQUIRED = "Car make is required";

    public static Long getFirstCarId() {
        return FIRST_CAR_ID;
    }

    public static Long getInvalidId() {
        return INVALID_CAR_ID;
    }

    public static Long getSecondCarId() {
        return SECOND_CAR_ID;
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

    public static Car getCarWithExistingNumber() {
        return Car.builder()
                .id(SECOND_CAR_ID)
                .number(EXISTING_CAR_NUMBER)
                .color(SECOND_CAR_COLOR)
                .carMake(SECOND_CAR_MAKE)
                .build();
    }

    public static CarRequest getCarRequest() {
        return CarRequest.builder()
                .number(NEW_CAR_NUMBER)
                .color(NEW_CAR_COLOR)
                .carMake(NEW_CAR_MAKE)
                .build();
    }

    public static CarRequest getCarRequestWithExistingNumber() {
        return CarRequest.builder()
                .number(EXISTING_CAR_NUMBER)
                .color(NEW_CAR_COLOR)
                .carMake(NEW_CAR_MAKE)
                .build();
    }


    public static CarRequest getCarRequestWithInvalidData() {
        return CarRequest.builder()
                .number("")
                .color("")
                .carMake("")
                .build();
    }

    public static CarResponse getFirstCarResponse() {
        return CarResponse.builder()
                .id(FIRST_CAR_ID)
                .number(FIRST_CAR_NUMBER)
                .color(FIRST_CAR_COLOR)
                .carMake(FIRST_CAR_MAKE)
                .build();
    }

    public static CarResponse getSecondCarResponse() {
        return CarResponse.builder()
                .id(SECOND_CAR_ID)
                .number(SECOND_CAR_NUMBER)
                .color(SECOND_CAR_COLOR)
                .carMake(SECOND_CAR_MAKE)
                .build();
    }

    public static CarResponse getNewCarResponse() {
        return CarResponse.builder()
                .id(NEW_CAR_ID)
                .number(NEW_CAR_NUMBER)
                .color(NEW_CAR_COLOR)
                .carMake(NEW_CAR_MAKE)
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

    public static List<Car> getCars() {
        return List.of(getFirstCar(), getSecondCar());
    }

    public static List<CarResponse> getCarResponses() {
        return List.of(getFirstCarResponse(), getSecondCarResponse());
    }

    public static ExceptionResponse getPhoneNumberExistsExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(CAR_NUMBER_EXIST, EXISTING_CAR_NUMBER))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public static ExceptionResponse getCarNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(CAR_NOT_FOUND, INVALID_CAR_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public static ValidationErrorResponse getValidationErrorResponse() {
        List<String> errors = new ArrayList<>();
        errors.add(CAR_MAKE_REQUIRED);
        errors.add(CAR_COLOR_REQUIRED);
        errors.add(CAR_NUMBER_REQUIRED);
        Collections.sort(errors);

        return ValidationErrorResponse.builder()
                .errors(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public static CarPageResponse getCarPageResponse() {
        return CarPageResponse.builder()
                .cars(List.of(getFirstCarResponse(), getSecondCarResponse()))
                .totalPages(2)
                .totalElements(4)
                .pageSize(PAGE_SIZE)
                .currentPage(PAGE_NUMBER)
                .build();
    }
}