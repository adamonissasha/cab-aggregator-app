package com.example.passengerservice.util;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;
import com.example.passengerservice.model.PassengerRating;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class TestPassengerRatingUtil {
    private final Long FIRST_PASSENGER_RATING_ID = 98L;
    private final Long PASSENGER_ID = 99L;
    private final Long FIRST_DRIVER_ID = 1L;
    private final Long FIRST_RIDE_ID = 1L;
    private final Integer FIRST_RATING = 5;
    private final Long SECOND_PASSENGER_RATING_ID = 99L;
    private final Long SECOND_DRIVER_ID = 2L;
    private final Long SECOND_RIDE_ID = 2L;
    private final Integer SECOND_RATING = 4;
    private final Double AVERAGE_RATING = 4.5;

    public Double getAveragePassengerRating() {
        return AVERAGE_RATING;
    }

    public PassengerRating getFirstPassengerRating() {
        return PassengerRating.builder()
                .id(FIRST_PASSENGER_RATING_ID)
                .driverId(FIRST_DRIVER_ID)
                .rideId(FIRST_RIDE_ID)
                .passenger(TestPassengerUtil.getFirstPassenger())
                .rating(FIRST_RATING)
                .build();
    }

    public PassengerRating getSecondPassengerRating() {
        return PassengerRating.builder()
                .id(SECOND_PASSENGER_RATING_ID)
                .driverId(SECOND_DRIVER_ID)
                .rideId(SECOND_RIDE_ID)
                .passenger(TestPassengerUtil.getSecondPassenger())
                .rating(SECOND_RATING)
                .build();
    }

    public PassengerRatingRequest getPassengerRatingRequest() {
        return PassengerRatingRequest.builder()
                .driverId(FIRST_DRIVER_ID)
                .passengerId(PASSENGER_ID)
                .rideId(FIRST_RIDE_ID)
                .rating(FIRST_RATING)
                .build();
    }

    public PassengerRatingResponse getFirstPassengerRatingResponse() {
        return PassengerRatingResponse.builder()
                .id(FIRST_PASSENGER_RATING_ID)
                .driverId(FIRST_DRIVER_ID)
                .passengerId(PASSENGER_ID)
                .rating(FIRST_RATING)
                .build();
    }

    public PassengerRatingResponse getSecondPassengerRatingResponse() {
        return PassengerRatingResponse.builder()
                .id(SECOND_PASSENGER_RATING_ID)
                .driverId(SECOND_DRIVER_ID)
                .passengerId(PASSENGER_ID)
                .rating(SECOND_RATING)
                .build();
    }

    public AveragePassengerRatingResponse getAveragePassengerRatingResponse() {
        return AveragePassengerRatingResponse.builder()
                .passengerId(PASSENGER_ID)
                .averageRating(AVERAGE_RATING)
                .build();
    }

    public AllPassengerRatingsResponse getAllPassengerRatingsResponse() {
        return AllPassengerRatingsResponse.builder()
                .passengerRatings(List.of(getFirstPassengerRatingResponse(), getSecondPassengerRatingResponse()))
                .build();
    }
}