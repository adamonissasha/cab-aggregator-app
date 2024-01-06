package com.example.ridesservice.util;


import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.request.RatingRequest;
import com.example.ridesservice.dto.request.StopRequest;
import com.example.ridesservice.dto.response.CarResponse;
import com.example.ridesservice.dto.response.DriverResponse;
import com.example.ridesservice.dto.response.PassengerResponse;
import com.example.ridesservice.dto.response.PassengerRideResponse;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.RideStatus;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestRideUtil {
    static Long FIRST_RIDE_ID = 1L;
    static String FIRST_START_ADDRESS = "Якуба Коласа, 28";
    static String FIRST_END_ADDRESS = "Платонова, 24";
    static Long FIRST_PASSENGER_ID = 1L;
    static PaymentMethod FIRST_PAYMENT_METHOD = PaymentMethod.CARD;
    static Long FIRST_BANK_CARD_ID = 1L;
    static PromoCode FIRST_PROMO_CODE = TestPromoCodeUtil.getFirstPromoCode();
    static BigDecimal FIRST_PRICE = BigDecimal.valueOf(12.5);
    static RideStatus FIRST_RIDE_STATUS = RideStatus.CREATED;
    static LocalDateTime FIRST_CREATION_DATE_TIME = LocalDateTime.now().minusDays(3);
    static Long FIRST_DRIVER_ID = 1L;
    static Long FIRST_CAR_ID = 1L;
    static LocalDateTime FIRST_START_DATE_TIME = FIRST_CREATION_DATE_TIME.plusMinutes(3);
    static LocalDateTime FIRST_END_DATE_TIME = FIRST_START_DATE_TIME.plusMinutes(20);
    static String PASSENGER_PHONE_NUMBER = "+375291234567";
    static String PASSENGER_NAME = "Sasha";
    static Double PASSENGER_RATING = 4.5;
    static String DRIVER_PHONE_NUMBER = "+375291234267";
    static String DRIVER_NAME = "Pasha";
    static Double DRIVER_RATING = 4.8;
    static String CAR_MAKE = "BMW";
    static String CAR_COLOR = "White";
    static String CAR_NUMBER = "1234-BM-7";
    static Integer RATING = 5;
    static List<StopRequest> STOP_REQUESTS = List.of(TestStopUtil.getFirstStopRequest(), TestStopUtil.getSecondStopRequest());
    static List<StopResponse> STOP_RESPONSES = List.of(TestStopUtil.getStopResponse(), TestStopUtil.getSecondStopResponse());

    public static Long getFirstRideId() {
        return FIRST_RIDE_ID;
    }

    public static List<StopResponse> getRideStopResponses() {
        return STOP_RESPONSES;
    }
    public static List<StopRequest> getRideStopRequests() {
        return STOP_REQUESTS;
    }

    public static PromoCode getFirstPromoCode() {
        return FIRST_PROMO_CODE;
    }

    public static Ride getFirstRide() {
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

    public static CreateRideRequest getCreateRideRequest() {
        return CreateRideRequest.builder()
                .startAddress(FIRST_START_ADDRESS)
                .endAddress(FIRST_END_ADDRESS)
                .passengerId(FIRST_PASSENGER_ID)
                .paymentMethod(FIRST_PAYMENT_METHOD.name())
                .bankCardId(FIRST_BANK_CARD_ID)
                .promoCode(FIRST_PROMO_CODE.getCode())
                .stops(STOP_REQUESTS)
                .build();
    }

    public static EditRideRequest getEditRideRequest() {
        return EditRideRequest.builder()
                .startAddress(FIRST_START_ADDRESS)
                .endAddress(FIRST_END_ADDRESS)
                .paymentMethod(FIRST_PAYMENT_METHOD.name())
                .stops(STOP_REQUESTS)
                .build();
    }

    public static RatingRequest getRatingRequest() {
        return RatingRequest.builder()
                .rating(RATING)
                .build();
    }

    public static RideResponse getFirstRideResponse() {
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

    public static PassengerRideResponse getPassengerRideResponse() {
        return PassengerRideResponse.builder()
                .rideId(FIRST_RIDE_ID)
                .passengerId(FIRST_PASSENGER_ID)
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

    public static PassengerResponse getPassengerResponse() {
        return PassengerResponse.builder()
                .id(FIRST_PASSENGER_ID)
                .firstName(PASSENGER_NAME)
                .phoneNumber(PASSENGER_PHONE_NUMBER)
                .rating(PASSENGER_RATING)
                .build();
    }

    public static DriverResponse getDriverResponse() {
        return DriverResponse.builder()
                .id(FIRST_DRIVER_ID)
                .firstName(DRIVER_NAME)
                .phoneNumber(DRIVER_PHONE_NUMBER)
                .rating(DRIVER_RATING)
                .car(CarResponse.builder()
                        .carMake(CAR_MAKE)
                        .color(CAR_COLOR)
                        .number(CAR_NUMBER)
                        .build())
                .build();
    }
}