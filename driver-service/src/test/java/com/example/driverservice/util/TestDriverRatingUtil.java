package com.example.driverservice.util;

import com.example.driverservice.dto.request.DriverRatingRequest;
import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.DriverRatingResponse;
import com.example.driverservice.model.DriverRating;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class TestDriverRatingUtil {
    private final Long FIRST_DRIVER_RATING_ID = 98L;
    private final Long DRIVER_ID = 99L;
    private final Long FIRST_PASSENGER_ID = 1L;
    private final Long FIRST_RIDE_ID = 1L;
    private final Integer FIRST_RATING = 5;
    private final Long SECOND_DRIVER_RATING_ID = 99L;
    private final Long SECOND_PASSENGER_ID = 2L;
    private final Long SECOND_RIDE_ID = 2L;
    private final Integer SECOND_RATING = 4;
    private final Double AVERAGE_RATING = 4.5;

    public Double getAverageDriverRating() {
        return AVERAGE_RATING;
    }

    public DriverRating getFirstDriverRating() {
        return DriverRating.builder()
                .id(FIRST_DRIVER_RATING_ID)
                .driver(TestDriverUtil.getFirstDriver())
                .rideId(FIRST_RIDE_ID)
                .passengerId(FIRST_PASSENGER_ID)
                .rating(FIRST_RATING)
                .build();
    }

    public DriverRating getSecondDriverRating() {
        return DriverRating.builder()
                .id(SECOND_DRIVER_RATING_ID)
                .driver(TestDriverUtil.getSecondDriver())
                .rideId(SECOND_RIDE_ID)
                .passengerId(SECOND_PASSENGER_ID)
                .rating(SECOND_RATING)
                .build();
    }

    public DriverRatingRequest getDriverRatingRequest() {
        return DriverRatingRequest.builder()
                .driverId(DRIVER_ID)
                .passengerId(FIRST_PASSENGER_ID)
                .rideId(FIRST_RIDE_ID)
                .rating(FIRST_RATING)
                .build();
    }

    public DriverRatingResponse getFirstDriverRatingResponse() {
        return DriverRatingResponse.builder()
                .id(FIRST_DRIVER_RATING_ID)
                .driverId(DRIVER_ID)
                .passengerId(FIRST_PASSENGER_ID)
                .rating(FIRST_RATING)
                .build();
    }

    public DriverRatingResponse getSecondDriverRatingResponse() {
        return DriverRatingResponse.builder()
                .id(SECOND_DRIVER_RATING_ID)
                .driverId(DRIVER_ID)
                .passengerId(SECOND_PASSENGER_ID)
                .rating(SECOND_RATING)
                .build();
    }

    public AllDriverRatingsResponse getAllDriverRatingsResponse() {
        DriverRatingResponse firstDriverRatingResponse = DriverRatingResponse.builder()
                .id(FIRST_DRIVER_RATING_ID)
                .driverId(DRIVER_ID)
                .passengerId(FIRST_PASSENGER_ID)
                .rating(FIRST_RATING)
                .build();
        DriverRatingResponse secondDriverRatingResponse = DriverRatingResponse.builder()
                .id(SECOND_DRIVER_RATING_ID)
                .driverId(DRIVER_ID)
                .passengerId(SECOND_PASSENGER_ID)
                .rating(SECOND_RATING)
                .build();
        return AllDriverRatingsResponse.builder()
                .driverRatings(List.of(firstDriverRatingResponse, secondDriverRatingResponse))
                .build();
    }

    public AverageDriverRatingResponse getAverageDriverRatingResponse() {
        return AverageDriverRatingResponse.builder()
                .driverId(DRIVER_ID)
                .averageRating(AVERAGE_RATING)
                .build();
    }
}