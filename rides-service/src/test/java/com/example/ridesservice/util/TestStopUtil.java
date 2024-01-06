package com.example.ridesservice.util;

import com.example.ridesservice.dto.request.StopRequest;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.model.Stop;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestStopUtil {
    static Long FIRST_RIDE_STOP_ID = 1L;
    static String FIRST_RIDE_ADDRESS = "Минск";
    static Integer FIRST_STOP_NUMBER = 1;
    static Long SECOND_RIDE_STOP_ID = 1L;
    static String SECOND_RIDE_ADDRESS = "Гродно";
    static Integer SECOND_STOP_NUMBER = 1;

    public static Stop getFirstStop() {
        return Stop.builder()
                .id(FIRST_RIDE_STOP_ID)
                .number(FIRST_STOP_NUMBER)
                .address(FIRST_RIDE_ADDRESS)
                .ride(TestRideUtil.getFirstRide())
                .build();
    }

    public static Stop getSecondStop() {
        return Stop.builder()
                .id(SECOND_RIDE_STOP_ID)
                .number(SECOND_STOP_NUMBER)
                .address(SECOND_RIDE_ADDRESS)
                .ride(TestRideUtil.getFirstRide())
                .build();
    }

    public static StopRequest getFirstStopRequest() {
        return StopRequest.builder()
                .address(FIRST_RIDE_ADDRESS)
                .number(FIRST_STOP_NUMBER)
                .build();
    }

    public static StopRequest getSecondStopRequest() {
        return StopRequest.builder()
                .address(SECOND_RIDE_ADDRESS)
                .number(SECOND_STOP_NUMBER)
                .build();
    }

    public static StopResponse getStopResponse() {
        return StopResponse.builder()
                .id(FIRST_RIDE_STOP_ID)
                .number(FIRST_STOP_NUMBER)
                .address(FIRST_RIDE_ADDRESS)
                .build();
    }

    public static StopResponse getSecondStopResponse() {
        return StopResponse.builder()
                .id(SECOND_RIDE_STOP_ID)
                .number(SECOND_STOP_NUMBER)
                .address(SECOND_RIDE_ADDRESS)
                .build();
    }
}