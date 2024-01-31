package com.example.driverservice.util;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarPageResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.dto.response.ValidationErrorResponse;
import com.example.driverservice.model.Car;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class TestCarUtil {
    private final Long NEW_CAR_ID = 100L;
    private final Long FIRST_CAR_ID = 98L;
    private final Long SECOND_CAR_ID = 99L;
    private final Long INVALID_CAR_ID = 199L;
    private final String FIRST_CAR_NUMBER = "1234-AA-1";
    private final String SECOND_CAR_NUMBER = "1234-BB-2";
    private final String NEW_CAR_NUMBER = "1235-BB-2";
    private final String FIRST_CAR_COLOR = "red";
    private final String SECOND_CAR_COLOR = "black";
    private final String NEW_CAR_COLOR = "white";
    private final String FIRST_CAR_MAKE = "BMW";
    private final String SECOND_CAR_MAKE = "Audi";
    private final String NEW_CAR_MAKE = "Volvo";
    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 2;
    private final String CORRECT_SORT_FIELD = "id";
    private final String INCORRECT_SORT_FIELD = "age";
    private final String CAR_NOT_FOUND = "Car with id '%s' not found";
    private final String CAR_NUMBER_EXIST = "Car with number '%s' already exist";
    private final String CAR_NUMBER_REQUIRED = "Car number is required";
    private final String CAR_COLOR_REQUIRED = "Car color is required";
    private final String CAR_MAKE_REQUIRED = "Car make is required";
    private final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: [id, number, color, carMake]";

    public Long getFirstCarId() {
        return FIRST_CAR_ID;
    }

    public Long getInvalidId() {
        return INVALID_CAR_ID;
    }

    public Long getSecondCarId() {
        return SECOND_CAR_ID;
    }

    public Car getFirstCar() {
        return Car.builder()
                .id(FIRST_CAR_ID)
                .number(FIRST_CAR_NUMBER)
                .color(FIRST_CAR_COLOR)
                .carMake(FIRST_CAR_MAKE)
                .build();
    }

    public Car getSecondCar() {
        return Car.builder()
                .id(SECOND_CAR_ID)
                .number(SECOND_CAR_NUMBER)
                .color(SECOND_CAR_COLOR)
                .carMake(SECOND_CAR_MAKE)
                .build();
    }

    public Car getCarWithExistingNumber() {
        return Car.builder()
                .id(SECOND_CAR_ID)
                .number(SECOND_CAR_NUMBER)
                .color(SECOND_CAR_COLOR)
                .carMake(SECOND_CAR_MAKE)
                .build();
    }

    public CarRequest getCarRequest() {
        return CarRequest.builder()
                .number(NEW_CAR_NUMBER)
                .color(NEW_CAR_COLOR)
                .carMake(NEW_CAR_MAKE)
                .build();
    }

    public CarRequest getCarRequestWithExistingNumber() {
        return CarRequest.builder()
                .number(SECOND_CAR_NUMBER)
                .color(NEW_CAR_COLOR)
                .carMake(NEW_CAR_MAKE)
                .build();
    }


    public CarRequest getCarRequestWithInvalidData() {
        return CarRequest.builder()
                .number("")
                .color("")
                .carMake("")
                .build();
    }

    public CarResponse getFirstCarResponse() {
        return CarResponse.builder()
                .id(FIRST_CAR_ID)
                .number(FIRST_CAR_NUMBER)
                .color(FIRST_CAR_COLOR)
                .carMake(FIRST_CAR_MAKE)
                .build();
    }

    public CarResponse getSecondCarResponse() {
        return CarResponse.builder()
                .id(SECOND_CAR_ID)
                .number(SECOND_CAR_NUMBER)
                .color(SECOND_CAR_COLOR)
                .carMake(SECOND_CAR_MAKE)
                .build();
    }

    public CarResponse getNewCarResponse() {
        return CarResponse.builder()
                .id(NEW_CAR_ID)
                .number(NEW_CAR_NUMBER)
                .color(NEW_CAR_COLOR)
                .carMake(NEW_CAR_MAKE)
                .build();
    }

    public int getPageNumber() {
        return PAGE_NUMBER;
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

    public String getCorrectSortField() {
        return CORRECT_SORT_FIELD;
    }

    public String getIncorrectSortField() {
        return INCORRECT_SORT_FIELD;
    }

    public List<Car> getCars() {
        return List.of(getFirstCar(), getSecondCar());
    }

    public List<CarResponse> getCarResponses() {
        return List.of(getFirstCarResponse(), getSecondCarResponse());
    }

    public ExceptionResponse getCarNumberExistsExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(CAR_NUMBER_EXIST, SECOND_CAR_NUMBER))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public ExceptionResponse getCarNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(CAR_NOT_FOUND, INVALID_CAR_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public ExceptionResponse getIncorrectFieldExceptionResponse() {
        return ExceptionResponse.builder()
                .message(INCORRECT_FIELDS)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public ValidationErrorResponse getValidationErrorResponse() {
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

    public CarPageResponse getCarPageResponse() {
        return CarPageResponse.builder()
                .cars(List.of(getFirstCarResponse(), getSecondCarResponse()))
                .totalPages(1)
                .totalElements(2)
                .pageSize(PAGE_SIZE)
                .currentPage(PAGE_NUMBER)
                .build();
    }
}