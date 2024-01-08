package com.example.passengerservice.util;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;
import com.example.passengerservice.model.PassengerRating;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestPassengerRatingUtil {
    static Long FIRST_PASSENGER_RATING_ID = 98L;
    static Long PASSENGER_ID = 99L;
    static Long FIRST_DRIVER_ID = 1L;
    static Long FIRST_RIDE_ID = 1L;
    static Integer FIRST_RATING = 5;
    static Long SECOND_PASSENGER_RATING_ID = 99L;
    static Long SECOND_DRIVER_ID = 2L;
    static Long SECOND_RIDE_ID = 2L;
    static Integer SECOND_RATING = 4;
    static Double AVERAGE_RATING = 4.5;

    public static Double getAveragePassengerRating() {
        return AVERAGE_RATING;
    }

    public static PassengerRating getFirstPassengerRating() {
        return PassengerRating.builder()
                .id(FIRST_PASSENGER_RATING_ID)
                .driverId(FIRST_DRIVER_ID)
                .rideId(FIRST_RIDE_ID)
                .passenger(TestPassengerUtil.getFirstPassenger())
                .rating(FIRST_RATING)
                .build();
    }

    public static PassengerRating getSecondPassengerRating() {
        return PassengerRating.builder()
                .id(SECOND_PASSENGER_RATING_ID)
                .driverId(SECOND_DRIVER_ID)
                .rideId(SECOND_RIDE_ID)
                .passenger(TestPassengerUtil.getSecondPassenger())
                .rating(SECOND_RATING)
                .build();
    }

    public static PassengerRatingRequest getPassengerRatingRequest() {
        return PassengerRatingRequest.builder()
                .driverId(FIRST_DRIVER_ID)
                .passengerId(PASSENGER_ID)
                .rideId(FIRST_RIDE_ID)
                .rating(FIRST_RATING)
                .build();
    }

    public static PassengerRatingResponse getFirstPassengerRatingResponse() {
        return PassengerRatingResponse.builder()
                .id(FIRST_PASSENGER_RATING_ID)
                .driverId(FIRST_DRIVER_ID)
                .passengerId(PASSENGER_ID)
                .rating(FIRST_RATING)
                .build();
    }

    public static PassengerRatingResponse getSecondPassengerRatingResponse() {
        return PassengerRatingResponse.builder()
                .id(SECOND_PASSENGER_RATING_ID)
                .driverId(SECOND_DRIVER_ID)
                .passengerId(PASSENGER_ID)
                .rating(SECOND_RATING)
                .build();
    }

    public static AveragePassengerRatingResponse getAveragePassengerRatingResponse() {
        return AveragePassengerRatingResponse.builder()
                .passengerId(PASSENGER_ID)
                .averageRating(AVERAGE_RATING)
                .build();
    }

    public static AllPassengerRatingsResponse getAllPassengerRatingsResponse() {
        return AllPassengerRatingsResponse.builder()
                .passengerRatings(List.of(getFirstPassengerRatingResponse(), getSecondPassengerRatingResponse()))
                .build();
    }
}