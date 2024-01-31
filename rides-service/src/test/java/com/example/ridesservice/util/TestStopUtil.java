package com.example.ridesservice.util;

import com.example.ridesservice.dto.request.StopRequest;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.model.Stop;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestStopUtil {
    private final Long FIRST_RIDE_STOP_ID = 98L;
    private final String FIRST_RIDE_ADDRESS = "Kupaly 88";
    private final Integer FIRST_STOP_NUMBER = 1;
    private final Long SECOND_RIDE_STOP_ID = 99L;
    private final String SECOND_RIDE_ADDRESS = "Brovki 1";
    private final Integer SECOND_STOP_NUMBER = 1;
    private final Long THIRD_RIDE_STOP_ID = 100L;

    public Stop getFirstStop() {
        return Stop.builder()
                .id(FIRST_RIDE_STOP_ID)
                .number(FIRST_STOP_NUMBER)
                .address(FIRST_RIDE_ADDRESS)
                .ride(TestRideUtil.getFirstRide())
                .build();
    }

    public Stop getSecondStop() {
        return Stop.builder()
                .id(SECOND_RIDE_STOP_ID)
                .number(SECOND_STOP_NUMBER)
                .address(SECOND_RIDE_ADDRESS)
                .ride(TestRideUtil.getFirstRide())
                .build();
    }

    public StopRequest getFirstStopRequest() {
        return StopRequest.builder()
                .address(FIRST_RIDE_ADDRESS)
                .number(FIRST_STOP_NUMBER)
                .build();
    }

    public StopRequest getSecondStopRequest() {
        return StopRequest.builder()
                .address(SECOND_RIDE_ADDRESS)
                .number(SECOND_STOP_NUMBER)
                .build();
    }

    public StopResponse getStopResponse() {
        return StopResponse.builder()
                .id(FIRST_RIDE_STOP_ID)
                .number(FIRST_STOP_NUMBER)
                .address(FIRST_RIDE_ADDRESS)
                .build();
    }

    public StopResponse getSecondStopResponse() {
        return StopResponse.builder()
                .id(SECOND_RIDE_STOP_ID)
                .number(SECOND_STOP_NUMBER)
                .address(SECOND_RIDE_ADDRESS)
                .build();
    }

    public StopResponse getThirdStopResponse() {
        return StopResponse.builder()
                .id(THIRD_RIDE_STOP_ID)
                .number(FIRST_STOP_NUMBER)
                .address(FIRST_RIDE_ADDRESS)
                .build();
    }
}