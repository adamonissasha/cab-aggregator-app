package com.example.driverservice.util;

import com.example.driverservice.dto.request.DriverRatingRequest;
import com.example.driverservice.dto.response.DriverRatingResponse;
import com.example.driverservice.model.DriverRating;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestDriverRatingUtil {
    static Long FIRST_DRIVER_RATING_ID = 1L;
    static Long FIRST_DRIVER_ID = 1L;
    static Long FIRST_PASSENGER_ID = 1L;
    static Long FIRST_RIDE_ID = 1L;
    static Integer FIRST_RATING = 5;
    static Long SECOND_DRIVER_RATING_ID = 2L;
    static Long SECOND_DRIVER_ID = 2L;
    static Long SECOND_PASSENGER_ID = 2L;
    static Long SECOND_RIDE_ID = 2L;
    static Integer SECOND_RATING = 4;
    static Double AVERAGE_RATING = 4.5;

    public static Double getAverageDriverRating() {
        return AVERAGE_RATING;
    }

    public static DriverRating getFirstDriverRating() {
        return DriverRating.builder()
                .id(FIRST_DRIVER_RATING_ID)
                .driver(TestDriverUtil.getFirstDriver())
                .rideId(FIRST_RIDE_ID)
                .passengerId(FIRST_PASSENGER_ID)
                .rating(FIRST_RATING)
                .build();
    }

    public static DriverRating getSecondDriverRating() {
        return DriverRating.builder()
                .id(SECOND_DRIVER_RATING_ID)
                .driver(TestDriverUtil.getSecondDriver())
                .rideId(SECOND_RIDE_ID)
                .passengerId(SECOND_PASSENGER_ID)
                .rating(SECOND_RATING)
                .build();
    }

    public static DriverRatingRequest getDriverRatingRequest() {
        return DriverRatingRequest.builder()
                .driverId(FIRST_DRIVER_ID)
                .passengerId(FIRST_PASSENGER_ID)
                .rideId(FIRST_RIDE_ID)
                .rating(FIRST_RATING)
                .build();
    }

    public static DriverRatingResponse getFirstDriverRatingResponse() {
        return DriverRatingResponse.builder()
                .id(FIRST_DRIVER_RATING_ID)
                .driverId(FIRST_DRIVER_ID)
                .passengerId(FIRST_PASSENGER_ID)
                .rating(FIRST_RATING)
                .build();
    }

    public static DriverRatingResponse getSecondDriverRatingResponse() {
        return DriverRatingResponse.builder()
                .id(SECOND_DRIVER_RATING_ID)
                .driverId(SECOND_DRIVER_ID)
                .passengerId(SECOND_PASSENGER_ID)
                .rating(SECOND_RATING)
                .build();
    }
}