package com.example.passengerservice.util;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.ValidationErrorResponse;
import com.example.passengerservice.model.Passenger;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class TestPassengerUtil {
    private final String FIRST_PASSENGER_ID = "65cbc3c1a85b366e0ef0564c";
    private final String SECOND_PASSENGER_ID = "65cbbb08e399fa178470785d";
    private final String INVALID_PASSENGER_ID = "65cbbb08e399fa118470785d";
    private final String FIRST_PASSENGER_FIRST_NAME = "Alex";
    private final String SECOND_PASSENGER_FIRST_NAME = "Pasha";
    private final String FIRST_PASSENGER_LAST_NAME = "Alexandrov";
    private final String SECOND_PASSENGER_LAST_NAME = "Pavlov";
    private final String FIRST_PASSENGER_EMAIL = "alex@gmail.com";
    private final String SECOND_PASSENGER_EMAIL = "pasha@gmail.com";
    private final String INVALID_PASSENGER_EMAIL = "alex";
    private final String FIRST_PASSENGER_PHONE_NUMBER = "+375297654321";
    private final String SECOND_PASSENGER_PHONE_NUMBER = "+375297654322";
    private final String INVALID_PHONE_NUMBER = "+728282828282";
    private final String UNIQUE_PHONE_NUMBER = "+375293621333";
    private final String FIRST_PASSENGER_PASSWORD = "321321321";
    private final String SECOND_PASSENGER_PASSWORD = "12345678";
    private final String INVALID_PASSENGER_PASSWORD = "321";
    private final Double FIRST_PASSENGER_RATING = 0.0;
    private final Double SECOND_PASSENGER_RATING = 4.5;
    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 2;
    private final String CORRECT_SORT_FIELD = "id";
    private final String INCORRECT_SORT_FIELD = "ids";
    private final String PASSENGER_NOT_FOUND = "Passenger with id '%s' not found";
    private final String PHONE_NUMBER_EXIST = "Passenger with phone number '%s' already exist";
    private final String PASSENGER_FIRST_NAME_REQUIRED_MESSAGE = "First name is required";
    private final String PASSENGER_LAST_NAME_REQUIRED_MESSAGE = "Last name is required";
    private final String PASSENGER_EMAIL_FORMAT_MESSAGE = "Invalid email format";
    private final String PASSENGER_PHONE_NUMBER_FORMAT_MESSAGE = "Phone number must match the format: +375xxxxxxxxx";
    private final String PASSENGER_PASSWORD_FORMAT_MESSAGE = "Password must contain at least one digit and be at least 8 characters long";
    private final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: [id, firstName, lastName, email, phoneNumber, password, isActive]";

    public String getFirstPassengerId() {
        return FIRST_PASSENGER_ID;
    }

    public String getSecondPassengerId() {
        return SECOND_PASSENGER_ID;
    }

    public String getInvalidId() {
        return INVALID_PASSENGER_ID;
    }

    public String getFirstPassengerPhoneNumber() {
        return FIRST_PASSENGER_PHONE_NUMBER;
    }

    public Passenger getFirstPassenger() {
        return Passenger.builder()
                .id(FIRST_PASSENGER_ID)
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(FIRST_PASSENGER_PHONE_NUMBER)
                .password(FIRST_PASSENGER_PASSWORD)
                .isActive(true)
                .build();
    }

    public Passenger getSecondPassenger() {
        return Passenger.builder()
                .id(SECOND_PASSENGER_ID)
                .firstName(SECOND_PASSENGER_FIRST_NAME)
                .lastName(SECOND_PASSENGER_LAST_NAME)
                .email(SECOND_PASSENGER_EMAIL)
                .phoneNumber(SECOND_PASSENGER_PHONE_NUMBER)
                .password(SECOND_PASSENGER_PASSWORD)
                .isActive(true)
                .build();
    }

    public PassengerRequest getPassengerRequest() {
        return PassengerRequest.builder()
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(FIRST_PASSENGER_PHONE_NUMBER)
                .password(FIRST_PASSENGER_PASSWORD)
                .build();
    }

    public PassengerRequest getUniquePassengerRequest() {
        return PassengerRequest.builder()
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(UNIQUE_PHONE_NUMBER)
                .password(FIRST_PASSENGER_PASSWORD)
                .build();
    }

    public PassengerRequest getPassengerRequestWithExistingNumber() {
        return PassengerRequest.builder()
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(FIRST_PASSENGER_PHONE_NUMBER)
                .password(FIRST_PASSENGER_PASSWORD)
                .build();
    }

    public PassengerRequest getPassengerRequestWithInvalidData() {
        return PassengerRequest.builder()
                .firstName("")
                .lastName("")
                .email(INVALID_PASSENGER_EMAIL)
                .phoneNumber(INVALID_PHONE_NUMBER)
                .password(INVALID_PASSENGER_PASSWORD)
                .build();
    }

    public PassengerResponse getPassengerResponse() {
        return PassengerResponse.builder()
                .id(FIRST_PASSENGER_ID)
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(FIRST_PASSENGER_PHONE_NUMBER)
                .rating(FIRST_PASSENGER_RATING)
                .isActive(true)
                .build();
    }

    public PassengerResponse getNewPassengerResponse() {
        return PassengerResponse.builder()
                .id(FIRST_PASSENGER_ID)
                .firstName(FIRST_PASSENGER_FIRST_NAME)
                .lastName(FIRST_PASSENGER_LAST_NAME)
                .email(FIRST_PASSENGER_EMAIL)
                .phoneNumber(UNIQUE_PHONE_NUMBER)
                .rating(0.0)
                .isActive(true)
                .build();
    }

    public PassengerResponse getSecondPassengerResponse() {
        return PassengerResponse.builder()
                .id(SECOND_PASSENGER_ID)
                .firstName(SECOND_PASSENGER_FIRST_NAME)
                .lastName(SECOND_PASSENGER_LAST_NAME)
                .email(SECOND_PASSENGER_EMAIL)
                .phoneNumber(SECOND_PASSENGER_PHONE_NUMBER)
                .rating(SECOND_PASSENGER_RATING)
                .isActive(true)
                .build();
    }

    public AveragePassengerRatingResponse getFirstPassengerRating() {
        return AveragePassengerRatingResponse.builder()
                .averageRating(FIRST_PASSENGER_RATING)
                .build();
    }

    public AveragePassengerRatingResponse getSecondPassengerRating() {
        return AveragePassengerRatingResponse.builder()
                .averageRating(SECOND_PASSENGER_RATING)
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

    public List<Passenger> getPassengers() {
        return List.of(getFirstPassenger(), getSecondPassenger());
    }

    public List<PassengerResponse> getPassengerResponses() {
        return List.of(getPassengerResponse(), getSecondPassengerResponse());
    }

    public ExceptionResponse getPhoneNumberExistsExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(PHONE_NUMBER_EXIST, FIRST_PASSENGER_PHONE_NUMBER))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public ExceptionResponse getPassengerNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(PASSENGER_NOT_FOUND, INVALID_PASSENGER_ID))
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

    public PassengerPageResponse getPassengerPageResponse() {
        return PassengerPageResponse.builder()
                .passengers(List.of(getPassengerResponse(), getSecondPassengerResponse()))
                .totalPages(1)
                .totalElements(2)
                .pageSize(PAGE_SIZE)
                .currentPage(PAGE_NUMBER)
                .build();
    }
}