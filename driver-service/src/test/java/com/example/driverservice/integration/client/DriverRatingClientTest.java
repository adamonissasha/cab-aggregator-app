package com.example.driverservice.integration.client;

import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
public class DriverRatingClientTest {
    private static final String DRIVER_RATING_SERVICE_URL = "driver/{id}/rating";
    private static final String ID_PARAMETER_NAME = "id";

    public AllDriverRatingsResponse getAllDriverRatingsWhenDriverExistsRequest(int port, Long driverId) {
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

    public ExceptionResponse getDriverRatingsWhenDriverNotExistsRequest(int port, Long invalidId) {
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

    public AverageDriverRatingResponse getAverageDriverRatingWhenDriverExistsRequest(int port, Long driverId) {
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

    public ExceptionResponse getAverageDriverRatingWhenDriverNotExistsRequest(int port, Long invalidId) {
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