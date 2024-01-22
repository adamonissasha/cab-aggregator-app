package com.example.driverservice.util;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.dto.response.ValidationErrorResponse;
import com.example.driverservice.model.Driver;
import com.example.driverservice.model.enums.Status;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestDriverUtil {
    static String CAR_NOT_FOUND_MESSAGE = "Car with id '%s' not found";
    static Long FIRST_DRIVER_ID = 98L;
    static Long SECOND_DRIVER_ID = 99L;
    static Long THIRD_DRIVER_ID = 100L;
    static Long INVALID_DRIVER_ID = 199L;
    static String FIRST_DRIVER_FIRST_NAME = "Ivan";
    static String SECOND_DRIVER_FIRST_NAME = "Alex";
    static String THIRD_DRIVER_FIRST_NAME = "Pasha";
    static String FIRST_DRIVER_LAST_NAME = "Ivanov";
    static String SECOND_DRIVER_LAST_NAME = "Alexandrov";
    static String THIRD_DRIVER_LAST_NAME = "Pavlov";
    static String FIRST_DRIVER_EMAIL = "ivan@gmail.com";
    static String SECOND_DRIVER_EMAIL = "alex@gmail.com";
    static String THIRD_DRIVER_EMAIL = "pasha@gmail.com";
    static String INVALID_DRIVER_EMAIL = "alex";
    static String FIRST_DRIVER_PHONE_NUMBER = "+375291234567";
    static String SECOND_DRIVER_PHONE_NUMBER = "+375297654321";
    static String THIRD_DRIVER_PHONE_NUMBER = "+375297654322";
    static String INVALID_PHONE_NUMBER = "+728282828282";
    static String FIRST_DRIVER_PASSWORD = "123123123";
    static String SECOND_DRIVER_PASSWORD = "321321312";
    static String INVALID_DRIVER_PASSWORD = "321";
    static Long FIRST_DRIVER_CAR_ID = 98L;
    static Double FIRST_DRIVER_RATING = 0.0;
    static Double SECOND_DRIVER_RATING = 4.5;
    static Double THIRD_DRIVER_RATING = 0.0;
    static int PAGE_NUMBER = 0;
    static int PAGE_SIZE = 2;
    static String CORRECT_SORT_FIELD = "id";
    static String INCORRECT_SORT_FIELD = "ids";
    static String PHONE_NUMBER_EXIST = "Driver with phone number '%s' already exist";
    static String DRIVER_NOT_FOUND = "Driver with id '%s' not found";
    static String DRIVER_FIRST_NAME_REQUIRED_MESSAGE = "First name is required";
    static String DRIVER_LAST_NAME_REQUIRED_MESSAGE = "Last name is required";
    static String DRIVER_EMAIL_FORMAT_MESSAGE = "Invalid email format";
    static String DRIVER_PHONE_NUMBER_FORMAT_MESSAGE = "Phone number must match the format: +375xxxxxxxxx";
    static String DRIVER_PASSWORD_FORMAT_MESSAGE = "Password must contain at least one digit and be at least 8 characters long";
    static String DRIVER_ALREADY_FREE = "Driver with id '%s' is already free";
    static String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: [id, firstName, lastName, email, phoneNumber, password, status, car, isActive]";

    public static Long getFirstDriverId() {
        return FIRST_DRIVER_ID;
    }

    public static Long getSecondDriverId() {
        return SECOND_DRIVER_ID;
    }

    public static Long getThirdDriverId() {
        return THIRD_DRIVER_ID;
    }

    public static Long getInvalidId() {
        return INVALID_DRIVER_ID;
    }

    public static String getFirstDriverPhoneNumber() {
        return FIRST_DRIVER_PHONE_NUMBER;
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
                .carId(FIRST_DRIVER_CAR_ID)
                .build();
    }


    public static DriverRequest getDriverRequestWithExistingNumber() {
        return DriverRequest.builder()
                .firstName(FIRST_DRIVER_FIRST_NAME)
                .lastName(FIRST_DRIVER_LAST_NAME)
                .email(FIRST_DRIVER_EMAIL)
                .phoneNumber(SECOND_DRIVER_PHONE_NUMBER)
                .password(FIRST_DRIVER_PASSWORD)
                .carId(FIRST_DRIVER_CAR_ID)
                .build();
    }

    public static DriverRequest getDriverRequestWithInvalidData() {
        return DriverRequest.builder()
                .firstName("")
                .lastName("")
                .email(INVALID_DRIVER_EMAIL)
                .phoneNumber(INVALID_PHONE_NUMBER)
                .password(INVALID_DRIVER_PASSWORD)
                .carId(FIRST_DRIVER_CAR_ID)
                .build();
    }

    public static DriverResponse getDriverResponse() {
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

    public static DriverResponse getSecondDriverResponse() {
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

    public static DriverResponse getThirdDriverResponse() {
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

    public static AverageDriverRatingResponse getFirstDriverRating() {
        return AverageDriverRatingResponse.builder()
                .averageRating(FIRST_DRIVER_RATING)
                .build();
    }

    public static AverageDriverRatingResponse getSecondDriverRating() {
        return AverageDriverRatingResponse.builder()
                .averageRating(SECOND_DRIVER_RATING)
                .build();
    }

    public static int getPageNumber() {
        return PAGE_NUMBER;
    }

    public static int getPageSize() {
        return PAGE_SIZE;
    }

    public static String getCorrectSortField() {
        return CORRECT_SORT_FIELD;
    }

    public static String getIncorrectSortField() {
        return INCORRECT_SORT_FIELD;
    }

    public static List<Driver> getDrivers() {
        return List.of(getFirstDriver(), getSecondDriver());
    }

    public static List<DriverResponse> getDriverResponses() {
        return List.of(getDriverResponse(), getSecondDriverResponse());
    }

    public static ExceptionResponse getPhoneNumberExistsExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(PHONE_NUMBER_EXIST, SECOND_DRIVER_PHONE_NUMBER))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public static ExceptionResponse getDriverNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(DRIVER_NOT_FOUND, INVALID_DRIVER_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public static ValidationErrorResponse getValidationErrorResponse() {
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

    public static ExceptionResponse getStatusExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(DRIVER_ALREADY_FREE, SECOND_DRIVER_ID))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public static ExceptionResponse getIncorrectFieldExceptionResponse() {
        return ExceptionResponse.builder()
                .message(INCORRECT_FIELDS)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public static DriverPageResponse getDriverPageResponse() {
        return DriverPageResponse.builder()
                .drivers(List.of(getSecondDriverResponse(), getThirdDriverResponse()))
                .totalPages(1)
                .totalElements(2)
                .pageSize(PAGE_SIZE)
                .currentPage(PAGE_NUMBER)
                .build();
    }
}