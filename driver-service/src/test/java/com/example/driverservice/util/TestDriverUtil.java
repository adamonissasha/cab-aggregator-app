package com.example.driverservice.util;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.dto.response.ValidationErrorResponse;
import com.example.driverservice.model.Driver;
import com.example.driverservice.model.enums.Status;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class TestDriverUtil {
    private final String CAR_NOT_FOUND_MESSAGE = "Car with id '%s' not found";
    private final Long FIRST_DRIVER_ID = 98L;
    private final Long SECOND_DRIVER_ID = 99L;
    private final Long THIRD_DRIVER_ID = 100L;
    private final Long INVALID_DRIVER_ID = 199L;
    private final String FIRST_DRIVER_FIRST_NAME = "Ivan";
    private final String SECOND_DRIVER_FIRST_NAME = "Alex";
    private final String THIRD_DRIVER_FIRST_NAME = "Pasha";
    private final String FIRST_DRIVER_LAST_NAME = "Ivanov";
    private final String SECOND_DRIVER_LAST_NAME = "Alexandrov";
    private final String THIRD_DRIVER_LAST_NAME = "Pavlov";
    private final String FIRST_DRIVER_EMAIL = "ivan@gmail.com";
    private final String SECOND_DRIVER_EMAIL = "alex@gmail.com";
    private final String THIRD_DRIVER_EMAIL = "pasha@gmail.com";
    private final String INVALID_DRIVER_EMAIL = "alex";
    private final String FIRST_DRIVER_PHONE_NUMBER = "+375291234567";
    private final String SECOND_DRIVER_PHONE_NUMBER = "+375297654321";
    private final String THIRD_DRIVER_PHONE_NUMBER = "+375297654322";
    private final String INVALID_PHONE_NUMBER = "+728282828282";
    private final String FIRST_DRIVER_PASSWORD = "123123123";
    private final String SECOND_DRIVER_PASSWORD = "321321312";
    private final String INVALID_DRIVER_PASSWORD = "321";
    private final Long FIRST_DRIVER_CAR_ID = 98L;
    private final Double FIRST_DRIVER_RATING = 0.0;
    private final Double SECOND_DRIVER_RATING = 4.5;
    private final Double THIRD_DRIVER_RATING = 0.0;
    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 2;
    private final String CORRECT_SORT_FIELD = "id";
    private final String INCORRECT_SORT_FIELD = "ids";
    private final String PHONE_NUMBER_EXIST = "Driver with phone number '%s' already exist";
    private final String DRIVER_NOT_FOUND = "Driver with id '%s' not found";
    private final String DRIVER_FIRST_NAME_REQUIRED_MESSAGE = "First name is required";
    private final String DRIVER_LAST_NAME_REQUIRED_MESSAGE = "Last name is required";
    private final String DRIVER_EMAIL_FORMAT_MESSAGE = "Invalid email format";
    private final String DRIVER_PHONE_NUMBER_FORMAT_MESSAGE = "Phone number must match the format: +375xxxxxxxxx";
    private final String DRIVER_PASSWORD_FORMAT_MESSAGE = "Password must contain at least one digit and be at least 8 characters long";
    private final String DRIVER_ALREADY_FREE = "Driver with id '%s' is already free";
    private final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: [id, firstName, lastName, email, phoneNumber, password, status, car, isActive]";

    public Long getFirstDriverId() {
        return FIRST_DRIVER_ID;
    }

    public Long getSecondDriverId() {
        return SECOND_DRIVER_ID;
    }

    public Long getThirdDriverId() {
        return THIRD_DRIVER_ID;
    }

    public Long getInvalidId() {
        return INVALID_DRIVER_ID;
    }

    public String getFirstDriverPhoneNumber() {
        return FIRST_DRIVER_PHONE_NUMBER;
    }

    public String getCarNotFoundMessage() {
        return CAR_NOT_FOUND_MESSAGE;
    }

    public Driver getFirstDriver() {
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

    public Driver getSecondDriver() {
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

    public DriverRequest getDriverRequest() {
        return DriverRequest.builder()
                .firstName(FIRST_DRIVER_FIRST_NAME)
                .lastName(FIRST_DRIVER_LAST_NAME)
                .email(FIRST_DRIVER_EMAIL)
                .phoneNumber(FIRST_DRIVER_PHONE_NUMBER)
                .password(FIRST_DRIVER_PASSWORD)
                .carId(FIRST_DRIVER_CAR_ID)
                .build();
    }


    public DriverRequest getDriverRequestWithExistingNumber() {
        return DriverRequest.builder()
                .firstName(FIRST_DRIVER_FIRST_NAME)
                .lastName(FIRST_DRIVER_LAST_NAME)
                .email(FIRST_DRIVER_EMAIL)
                .phoneNumber(SECOND_DRIVER_PHONE_NUMBER)
                .password(FIRST_DRIVER_PASSWORD)
                .carId(FIRST_DRIVER_CAR_ID)
                .build();
    }

    public DriverRequest getDriverRequestWithInvalidData() {
        return DriverRequest.builder()
                .firstName("")
                .lastName("")
                .email(INVALID_DRIVER_EMAIL)
                .phoneNumber(INVALID_PHONE_NUMBER)
                .password(INVALID_DRIVER_PASSWORD)
                .carId(FIRST_DRIVER_CAR_ID)
                .build();
    }

    public DriverResponse getDriverResponse() {
        return DriverResponse.builder()
                .id(FIRST_DRIVER_ID)
                .firstName(FIRST_DRIVER_FIRST_NAME)
                .lastName(FIRST_DRIVER_LAST_NAME)
                .email(FIRST_DRIVER_EMAIL)
                .phoneNumber(FIRST_DRIVER_PHONE_NUMBER)
                .car(TestCarUtil.getFirstCarResponse())
                .status(Status.FREE.name())
                .rating(FIRST_DRIVER_RATING)
                .isActive(true)
                .build();
    }

    public DriverResponse getSecondDriverResponse() {
        return DriverResponse.builder()
                .id(SECOND_DRIVER_ID)
                .firstName(SECOND_DRIVER_FIRST_NAME)
                .lastName(SECOND_DRIVER_LAST_NAME)
                .email(SECOND_DRIVER_EMAIL)
                .phoneNumber(SECOND_DRIVER_PHONE_NUMBER)
                .car(TestCarUtil.getFirstCarResponse())
                .status(Status.FREE.name())
                .rating(SECOND_DRIVER_RATING)
                .isActive(true)
                .build();
    }

    public DriverResponse getThirdDriverResponse() {
        return DriverResponse.builder()
                .id(THIRD_DRIVER_ID)
                .firstName(THIRD_DRIVER_FIRST_NAME)
                .lastName(THIRD_DRIVER_LAST_NAME)
                .email(THIRD_DRIVER_EMAIL)
                .phoneNumber(THIRD_DRIVER_PHONE_NUMBER)
                .car(TestCarUtil.getSecondCarResponse())
                .rating(THIRD_DRIVER_RATING)
                .status(Status.BUSY.name())
                .isActive(true)
                .build();
    }

    public AverageDriverRatingResponse getFirstDriverRating() {
        return AverageDriverRatingResponse.builder()
                .averageRating(FIRST_DRIVER_RATING)
                .build();
    }

    public AverageDriverRatingResponse getSecondDriverRating() {
        return AverageDriverRatingResponse.builder()
                .averageRating(SECOND_DRIVER_RATING)
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

    public List<Driver> getDrivers() {
        return List.of(getFirstDriver(), getSecondDriver());
    }

    public List<DriverResponse> getDriverResponses() {
        return List.of(getDriverResponse(), getSecondDriverResponse());
    }

    public ExceptionResponse getPhoneNumberExistsExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(PHONE_NUMBER_EXIST, SECOND_DRIVER_PHONE_NUMBER))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public ExceptionResponse getDriverNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(DRIVER_NOT_FOUND, INVALID_DRIVER_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public ValidationErrorResponse getValidationErrorResponse() {
        List<String> errors = new ArrayList<>();
        errors.add(DRIVER_PHONE_NUMBER_FORMAT_MESSAGE);
        errors.add(DRIVER_LAST_NAME_REQUIRED_MESSAGE);
        errors.add(DRIVER_EMAIL_FORMAT_MESSAGE);
        errors.add(DRIVER_FIRST_NAME_REQUIRED_MESSAGE);
        errors.add(DRIVER_PASSWORD_FORMAT_MESSAGE);
        Collections.sort(errors);

        return ValidationErrorResponse.builder()
                .errors(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public ExceptionResponse getStatusExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(DRIVER_ALREADY_FREE, SECOND_DRIVER_ID))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public ExceptionResponse getIncorrectFieldExceptionResponse() {
        return ExceptionResponse.builder()
                .message(INCORRECT_FIELDS)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public DriverPageResponse getDriverPageResponse() {
        return DriverPageResponse.builder()
                .drivers(List.of(getSecondDriverResponse(), getThirdDriverResponse()))
                .totalPages(1)
                .totalElements(2)
                .pageSize(PAGE_SIZE)
                .currentPage(PAGE_NUMBER)
                .build();
    }
}