package com.example.driverservice.util.client;

import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@UtilityClass
public class DriverRatingClientUtil {
    private final String DRIVER_RATING_SERVICE_URL = "driver/{id}/rating";
    private final String ID_PARAMETER_NAME = "id";

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