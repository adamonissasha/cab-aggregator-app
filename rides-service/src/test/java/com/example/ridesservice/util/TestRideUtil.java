package com.example.ridesservice.util;


import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.request.RatingRequest;
import com.example.ridesservice.dto.request.StopRequest;
import com.example.ridesservice.dto.response.CarResponse;
import com.example.ridesservice.dto.response.DriverResponse;
import com.example.ridesservice.dto.response.ExceptionResponse;
import com.example.ridesservice.dto.response.PassengerResponse;
import com.example.ridesservice.dto.response.PassengerRideResponse;
import com.example.ridesservice.dto.response.PassengerRidesPageResponse;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.dto.response.RidesPageResponse;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.dto.response.ValidationErrorResponse;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.RideStatus;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class TestRideUtil {
    private final Long FIRST_RIDE_ID = 98L;
    private final Long SECOND_RIDE_ID = 99L;
    private final Long THIRD_RIDE_ID = 100L;
    private final Long FOURTH_RIDE_ID = 101L;
    private final Long INVALID_RIDE_ID = 111L;
    private final String FIRST_START_ADDRESS = "Platonova 49";
    private final String SECOND_START_ADDRESS = "Sovetskaya 12";
    private final String FIRST_END_ADDRESS = "Masherova 113";
    private final String SECOND_END_ADDRESS = "Lenina 89";
    private final Long NEW_PASSENGER_ID = 3L;
    private final Long FIRST_PASSENGER_ID = 13L;
    private final PaymentMethod FIRST_PAYMENT_METHOD = PaymentMethod.CARD;
    private final PaymentMethod SECOND_PAYMENT_METHOD = PaymentMethod.CASH;
    private final Long FIRST_BANK_CARD_ID = 1L;
    private final PromoCode FIRST_PROMO_CODE = TestPromoCodeUtil.getSecondPromoCode();
    private final BigDecimal FIRST_PRICE = BigDecimal.valueOf(12.5);
    private final RideStatus FIRST_RIDE_STATUS = RideStatus.CREATED;
    private final LocalDateTime FIRST_CREATION_DATE_TIME = LocalDateTime.now();
    private final Long FIRST_DRIVER_ID = 1L;
    private final Long FIRST_CAR_ID = 1L;
    private final LocalDateTime FIRST_START_DATE_TIME = null;
    private final LocalDateTime FIRST_END_DATE_TIME = null;
    private final String PASSENGER_PHONE_NUMBER = "+375291234967";
    private final String PASSENGER_NAME = "Ivan";
    private final Double PASSENGER_RATING = 0.0;
    private final String DRIVER_PHONE_NUMBER = "+375099490457";
    private final String DRIVER_NAME = "Sasha";
    private final Double DRIVER_RATING = 4.5;
    private final long CAR_ID = 1L;
    private final String CAR_MAKE = "Audi";
    private final String CAR_COLOR = "red";
    private final String CAR_NUMBER = "1961-DF-7";
    private final Integer RATING = 5;
    private final List<StopRequest> STOP_REQUESTS = List.of(TestStopUtil.getFirstStopRequest());
    private final List<StopResponse> STOP_RESPONSES = List.of(TestStopUtil.getStopResponse());
    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 2;
    private final String CORRECT_SORT_FIELD = "id";
    private final String INCORRECT_SORT_FIELD = "age";
    private final String CARD_PAYMENT_METHOD = "If you have chosen CARD payment method, select the card for payment";
    private final String PASSENGER_RIDE_EXCEPTION = "Passenger with id '%s' has already book a ride";
    private final String START_ADDRESS_REQUIRED = "Start address is required";
    private final String PAYMENT_METHOD_REQUIRED = "Payment method is required";
    private final String RIDE_NOT_FOUND = "Ride with id '%s' not found";
    private final String STATUS_EXCEPTION = "The ride with id '%s' has already been canceled";
    private final String INCORRECT_FIELDS = "Invalid sortBy field. Allowed fields: [id, startAddress, endAddress, passengerId, paymentMethod, bankCardId, promoCode, price, status, creationDateTime, driverId, carId, startDateTime, endDateTime]";

    public Long getFirstRideId() {
        return FIRST_RIDE_ID;
    }

    public Long getSecondRideId() {
        return SECOND_RIDE_ID;
    }

    public Long getThirdRideId() {
        return THIRD_RIDE_ID;
    }

    public Long getFourthRideId() {
        return FOURTH_RIDE_ID;
    }

    public Long getInvalidRideId() {
        return INVALID_RIDE_ID;
    }

    public Long getFirstPassengerId() {
        return FIRST_PASSENGER_ID;
    }

    public Long getFirstDriverId() {
        return FIRST_DRIVER_ID;
    }

    public List<StopResponse> getRideStopResponses() {
        return STOP_RESPONSES;
    }

    public List<StopRequest> getRideStopRequests() {
        return STOP_REQUESTS;
    }

    public PromoCode getFirstPromoCode() {
        return FIRST_PROMO_CODE;
    }

    public Ride getFirstRide() {
        return Ride.builder()
                .id(FIRST_RIDE_ID)
                .startAddress(FIRST_START_ADDRESS)
                .endAddress(FIRST_END_ADDRESS)
                .passengerId(FIRST_PASSENGER_ID)
                .paymentMethod(FIRST_PAYMENT_METHOD)
                .bankCardId(FIRST_BANK_CARD_ID)
                .promoCode(FIRST_PROMO_CODE)
                .price(FIRST_PRICE)
                .status(FIRST_RIDE_STATUS)
                .creationDateTime(FIRST_CREATION_DATE_TIME)
                .driverId(FIRST_DRIVER_ID)
                .carId(FIRST_CAR_ID)
                .startDateTime(FIRST_START_DATE_TIME)
                .endDateTime(FIRST_END_DATE_TIME)
                .build();
    }

    public CreateRideRequest getCreateRideRequest() {
        return CreateRideRequest.builder()
                .startAddress(FIRST_START_ADDRESS)
                .endAddress(FIRST_END_ADDRESS)
                .passengerId(NEW_PASSENGER_ID)
                .paymentMethod(FIRST_PAYMENT_METHOD.name())
                .bankCardId(FIRST_BANK_CARD_ID)
                .promoCode(FIRST_PROMO_CODE.getCode())
                .stops(STOP_REQUESTS)
                .build();
    }

    public CreateRideRequest getCreateRideRequestWithIncorrectPaymentMethod() {
        CreateRideRequest createRideRequest = getCreateRideRequest();
        createRideRequest.setBankCardId(null);
        return createRideRequest;
    }

    public CreateRideRequest getCreateExistingRideRequest() {
        CreateRideRequest createRideRequest = getCreateRideRequest();
        createRideRequest.setPassengerId(FIRST_PASSENGER_ID);
        return createRideRequest;
    }

    public CreateRideRequest getCreateRideRequestWithInvalidData() {
        CreateRideRequest createRideRequest = getCreateRideRequest();
        createRideRequest.setStartAddress(null);
        createRideRequest.setPaymentMethod(null);
        return createRideRequest;
    }

    public EditRideRequest getEditRideRequest() {
        return EditRideRequest.builder()
                .startAddress(SECOND_START_ADDRESS)
                .endAddress(SECOND_END_ADDRESS)
                .paymentMethod(SECOND_PAYMENT_METHOD.name())
                .build();
    }

    public EditRideRequest getEditRideRequestWithInvalidData() {
        EditRideRequest editRideRequest = getEditRideRequest();
        editRideRequest.setStartAddress(null);
        editRideRequest.setPaymentMethod(null);
        return editRideRequest;
    }

    public RatingRequest getRatingRequest() {
        return RatingRequest.builder()
                .rating(RATING)
                .build();
    }

    public RideResponse getFirstRideResponse() {
        return RideResponse.builder()
                .rideId(FIRST_RIDE_ID)
                .passengerName(PASSENGER_NAME)
                .passengerPhoneNumber(PASSENGER_PHONE_NUMBER)
                .passengerRating(PASSENGER_RATING)
                .driverName(DRIVER_NAME)
                .driverPhoneNumber(DRIVER_PHONE_NUMBER)
                .driverRating(DRIVER_RATING)
                .carNumber(CAR_NUMBER)
                .carMake(CAR_MAKE)
                .carColor(CAR_COLOR)
                .startAddress(FIRST_START_ADDRESS)
                .endAddress(FIRST_END_ADDRESS)
                .price(FIRST_PRICE)
                .creationDateTime(FIRST_CREATION_DATE_TIME)
                .startDateTime(FIRST_START_DATE_TIME)
                .endDateTime(FIRST_END_DATE_TIME)
                .status(FIRST_RIDE_STATUS.name())
                .stops(STOP_RESPONSES)
                .promoCode(FIRST_PROMO_CODE.getCode())
                .paymentMethod(FIRST_PAYMENT_METHOD.name())
                .build();
    }

    public RideResponse getSecondRideResponse() {
        RideResponse rideResponse = getFirstRideResponse();
        rideResponse.setRideId(SECOND_RIDE_ID);
        rideResponse.setStatus(RideStatus.CANCELED.name());
        return rideResponse;
    }

    public RideResponse getStartedRideResponse() {
        RideResponse rideResponse = getFirstRideResponse();
        rideResponse.setStatus(RideStatus.STARTED.name());
        rideResponse.setStartDateTime(LocalDateTime.now());
        return rideResponse;
    }

    public RideResponse getCanceledRideResponse() {
        RideResponse rideResponse = getFirstRideResponse();
        rideResponse.setStatus(RideStatus.CANCELED.name());
        return rideResponse;
    }

    public RideResponse getCompletedRideResponse() {
        RideResponse rideResponse = getFirstRideResponse();
        rideResponse.setStops(List.of(TestStopUtil.getThirdStopResponse()));
        rideResponse.setRideId(THIRD_RIDE_ID);
        rideResponse.setStatus(RideStatus.COMPLETED.name());
        rideResponse.setStartDateTime(LocalDateTime.now());
        rideResponse.setEndDateTime(LocalDateTime.now());
        return rideResponse;
    }

    public PassengerRideResponse getPassengerRideResponse() {
        return PassengerRideResponse.builder()
                .rideId(FIRST_RIDE_ID)
                .passengerId(NEW_PASSENGER_ID)
                .driverName(DRIVER_NAME)
                .driverPhoneNumber(DRIVER_PHONE_NUMBER)
                .driverRating(DRIVER_RATING)
                .carNumber(CAR_NUMBER)
                .carMake(CAR_MAKE)
                .carColor(CAR_COLOR)
                .startAddress(FIRST_START_ADDRESS)
                .endAddress(FIRST_END_ADDRESS)
                .price(FIRST_PRICE)
                .creationDateTime(FIRST_CREATION_DATE_TIME)
                .startDateTime(FIRST_START_DATE_TIME)
                .endDateTime(FIRST_END_DATE_TIME)
                .status(FIRST_RIDE_STATUS.name())
                .stops(STOP_RESPONSES)
                .promoCode(FIRST_PROMO_CODE.getCode())
                .paymentMethod(FIRST_PAYMENT_METHOD.name())
                .build();
    }

    public PassengerRideResponse getSecondPassengerRideResponse() {
        PassengerRideResponse passengerRideResponse = getPassengerRideResponse();
        passengerRideResponse.setRideId(SECOND_RIDE_ID);
        passengerRideResponse.setStatus(RideStatus.CANCELED.name());
        return passengerRideResponse;
    }

    public PassengerRideResponse getEditedPassengerRideResponse() {
        PassengerRideResponse passengerRideResponse = getPassengerRideResponse();
        passengerRideResponse.setPassengerId(FIRST_PASSENGER_ID);
        passengerRideResponse.setStartAddress(SECOND_START_ADDRESS);
        passengerRideResponse.setEndAddress(SECOND_END_ADDRESS);
        passengerRideResponse.setPaymentMethod(SECOND_PAYMENT_METHOD.name());
        return passengerRideResponse;
    }

    public PassengerResponse getPassengerResponse() {
        return PassengerResponse.builder()
                .id(FIRST_PASSENGER_ID)
                .firstName(PASSENGER_NAME)
                .phoneNumber(PASSENGER_PHONE_NUMBER)
                .rating(PASSENGER_RATING)
                .build();
    }

    public DriverResponse getDriverResponse() {
        return DriverResponse.builder()
                .id(FIRST_DRIVER_ID)
                .firstName(DRIVER_NAME)
                .phoneNumber(DRIVER_PHONE_NUMBER)
                .rating(DRIVER_RATING)
                .car(CarResponse.builder()
                        .id(CAR_ID)
                        .carMake(CAR_MAKE)
                        .color(CAR_COLOR)
                        .number(CAR_NUMBER)
                        .build())
                .build();
    }

    public ExceptionResponse getPaymentMethodExceptionResponse() {
        return ExceptionResponse.builder()
                .message(CARD_PAYMENT_METHOD)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public ExceptionResponse getPassengerExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(PASSENGER_RIDE_EXCEPTION, FIRST_PASSENGER_ID))
                .statusCode(HttpStatus.CONFLICT.value())
                .build();
    }

    public ExceptionResponse getRideNotFoundExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(RIDE_NOT_FOUND, INVALID_RIDE_ID))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }


    public ExceptionResponse getStatusExceptionResponse() {
        return ExceptionResponse.builder()
                .message(String.format(STATUS_EXCEPTION, SECOND_RIDE_ID))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public ValidationErrorResponse getValidationErrorResponse() {
        List<String> errors = new ArrayList<>();
        errors.add(START_ADDRESS_REQUIRED);
        errors.add(PAYMENT_METHOD_REQUIRED);
        Collections.sort(errors);

        return ValidationErrorResponse.builder()
                .errors(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
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

    public PassengerRidesPageResponse getPassengerRidesPageResponse() {
        return PassengerRidesPageResponse.builder()
                .rides(List.of(getPassengerRideResponse(), getSecondPassengerRideResponse()))
                .totalPages(2)
                .totalElements(4)
                .pageSize(PAGE_SIZE)
                .currentPage(PAGE_NUMBER)
                .build();
    }

    public RidesPageResponse getDriverRidesPageResponse() {
        return RidesPageResponse.builder()
                .rides(List.of(getFirstRideResponse(), getSecondRideResponse()))
                .totalPages(2)
                .totalElements(4)
                .pageSize(PAGE_SIZE)
                .currentPage(PAGE_NUMBER)
                .build();
    }

    public ExceptionResponse getIncorrectFieldExceptionResponse() {
        return ExceptionResponse.builder()
                .message(INCORRECT_FIELDS)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}