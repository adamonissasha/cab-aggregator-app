package com.example.passengerservice.util;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.ValidationErrorResponse;
import com.example.passengerservice.model.Passenger;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestPassengerUtil {
    static Long FIRST_PASSENGER_ID = 99L;
    static Long SECOND_PASSENGER_ID = 100L;
    static Long INVALID_PASSENGER_ID = 123L;
    static String FIRST_PASSENGER_FIRST_NAME = "Alex";
    static String SECOND_PASSENGER_FIRST_NAME = "Pasha";
    static String FIRST_PASSENGER_LAST_NAME = "Alexandrov";
    static String SECOND_PASSENGER_LAST_NAME = "Pavlov";
    static String FIRST_PASSENGER_EMAIL = "alex@gmail.com";
    static String SECOND_PASSENGER_EMAIL = "pasha@gmail.com";
    static String INVALID_PASSENGER_EMAIL = "alex";
    static String FIRST_PASSENGER_PHONE_NUMBER = "+375297654321";
    static String SECOND_PASSENGER_PHONE_NUMBER = "+375297654322";
    static String INVALID_PHONE_NUMBER = "+728282828282";
    static String UNIQUE_PHONE_NUMBER = "+375293621333";
    static String FIRST_PASSENGER_PASSWORD = "321321321";
    static String SECOND_PASSENGER_PASSWORD = "12345678";
    static String INVALID_PASSENGER_PASSWORD = "321";
    static Double FIRST_PASSENGER_RATING = 4.5;
    static Double SECOND_PASSENGER_RATING = 0.0;
    static int PAGE_NUMBER = 0;
    static int PAGE_SIZE = 2;
    static String CORRECT_SORT_FIELD = "id";
    static String INCORRECT_SORT_FIELD = "ids";
    static String PASSENGER_NOT_FOUND = "Passenger with id '%s' not found";
    static String PHONE_NUMBER_EXIST = "Passenger with phone number '%s' already exist";
    static String PASSENGER_FIRST_NAME_REQUIRED_MESSAGE = "First name is required";
    static String PASSENGER_LAST_NAME_REQUIRED_MESSAGE = "Last name is required";
    static String PASSENGER_EMAIL_FORMAT_MESSAGE = "Invalid email format";
    static String PASSENGER_PHONE_NUMBER_FORMAT_MESSAGE = "Phone number must match the format: +375xxxxxxxxx";
    static String PASSENGER_PASSWORD_FORMAT_MESSAGE = "Password must contain at least one digit and be at least 8 characters long";
    static String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: [id, firstName, lastName, email, phoneNumber, password, isActive]";

    public static Long getFirstPassengerId() {
        return FIRST_PASSENGER_ID;
    }

    public static Long getSecondPassengerId() {
        return SECOND_PASSENGER_ID;
    }

    public static Long getInvalidId() {
        return INVALID_PASSENGER_ID;
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

    public static PassengerRequest getUniquePassengerRequest() {
        return PassengerRequest.builder()
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(UNIQUE_PHONE_NUMBER)
                .password(FIRST_PASSENGER_PASSWORD)
                .build();
    }

    public static PassengerRequest getPassengerRequestWithExistingNumber() {
        return PassengerRequest.builder()
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(FIRST_PASSENGER_PHONE_NUMBER)
                .password(FIRST_PASSENGER_PASSWORD)
                .build();
    }

    public static PassengerRequest getPassengerRequestWithInvalidData() {
        return PassengerRequest.builder()
                .firstName("")
                .lastName("")
                .email(INVALID_PASSENGER_EMAIL)
                .phoneNumber(INVALID_PHONE_NUMBER)
                .password(INVALID_PASSENGER_PASSWORD)
                .build();
    }

    public static PassengerResponse getPassengerResponse() {
        return PassengerResponse.builder()
                .id(FIRST_PASSENGER_ID)
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(FIRST_PASSENGER_PHONE_NUMBER)
                .rating(FIRST_PASSENGER_RATING)
                .build();
    }

    public static PassengerResponse getNewPassengerResponse() {
        return PassengerResponse.builder()
                .id(FIRST_PASSENGER_ID)
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(UNIQUE_PHONE_NUMBER)
                .rating(SECOND_PASSENGER_RATING)
                .build();
    }

    public static PassengerResponse getSecondPassengerResponse() {
        return PassengerResponse.builder()
                .id(SECOND_PASSENGER_ID)
                .firstName(SECOND_PASSENGER_FIRST_NAME)
                .lastName(SECOND_PASSENGER_LAST_NAME)
                .email(SECOND_PASSENGER_EMAIL)
                .phoneNumber(SECOND_PASSENGER_PHONE_NUMBER)
                .rating(SECOND_PASSENGER_RATING)
                .build();
    }

    public static AveragePassengerRatingResponse getFirstPassengerRating() {
        return AveragePassengerRatingResponse.builder()
                .averageRating(FIRST_PASSENGER_RATING)
                .build();
    }

    public static AveragePassengerRatingResponse getSecondPassengerRating() {
        return AveragePassengerRatingResponse.builder()
                .averageRating(SECOND_PASSENGER_RATING)
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

    public static List<Passenger> getPassengers() {
        return List.of(getFirstPassenger(), getSecondPassenger());
    }

    public static List<PassengerResponse> getPassengerResponses() {
        return List.of(getPassengerResponse(), getSecondPassengerResponse());
    }

    public static ExceptionResponse getPhoneNumberExistsExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(PHONE_NUMBER_EXIST, FIRST_PASSENGER_PHONE_NUMBER))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public static ExceptionResponse getPassengerNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(PASSENGER_NOT_FOUND, INVALID_PASSENGER_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    public static ExceptionResponse getIncorrectFieldExceptionResponse() {
        return ExceptionResponse.builder()
                .message(INCORRECT_FIELDS)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public static ValidationErrorResponse getValidationErrorResponse() {
        List<String> errors = new ArrayList<>();
        errors.add(PASSENGER_PHONE_NUMBER_FORMAT_MESSAGE);
        errors.add(PASSENGER_LAST_NAME_REQUIRED_MESSAGE);
        errors.add(PASSENGER_EMAIL_FORMAT_MESSAGE);
        errors.add(PASSENGER_FIRST_NAME_REQUIRED_MESSAGE);
        errors.add(PASSENGER_PASSWORD_FORMAT_MESSAGE);
        Collections.sort(errors);

        return ValidationErrorResponse.builder()
                .errors(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public static PassengerPageResponse getPassengerPageResponse() {
        return PassengerPageResponse.builder()
                .passengers(List.of(getPassengerResponse(), getSecondPassengerResponse()))
                .totalPages(1)
                .totalElements(2)
                .pageSize(PAGE_SIZE)
                .currentPage(PAGE_NUMBER)
                .build();
    }
}