package com.example.driverservice.util.client;

import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DriverRatingClientUtil {
    static String DRIVER_RATING_SERVICE_URL = "driver/{id}/rating";
    static String ID_PARAMETER_NAME = "id";

    public static AllDriverRatingsResponse getAllDriverRatingsWhenDriverExistsRequest(int port, Long driverId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, driverId)
                .when()
                .get(DRIVER_RATING_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AllDriverRatingsResponse.class);
    }

    public static ExceptionResponse getDriverRatingsWhenDriverNotExistsRequest(int port, Long invalidId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidId)
                .when()
                .get(DRIVER_RATING_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static AverageDriverRatingResponse getAverageDriverRatingWhenDriverExistsRequest(int port, Long driverId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, driverId)
                .when()
                .get(DRIVER_RATING_SERVICE_URL + "/average")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AverageDriverRatingResponse.class);
    }

    public static ExceptionResponse getAverageDriverRatingWhenDriverNotExistsRequest(int port, Long invalidId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidId)
                .when()
                .get(DRIVER_RATING_SERVICE_URL + "/average")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }
}