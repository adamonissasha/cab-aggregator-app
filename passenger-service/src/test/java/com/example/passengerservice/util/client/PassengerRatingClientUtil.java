package com.example.passengerservice.util.client;

import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.ExceptionResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@UtilityClass
public class PassengerRatingClientUtil {
    private final String PASSENGER_RATING_SERVICE_URL = "passenger/{id}/rating";
    private final String ID_PARAMETER_NAME = "id";

    public AllPassengerRatingsResponse getAllPassengerRatingsWhenPassengerExistsRequest(int port, String passengerId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, passengerId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AllPassengerRatingsResponse.class);
    }

    public ExceptionResponse getPassengerRatingsWhenPassengerNotExistsRequest(int port, String invalidId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public AveragePassengerRatingResponse getAveragePassengerRatingWhenPassengerExistsRequest(int port, String passengerId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, passengerId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL + "/average")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AveragePassengerRatingResponse.class);
    }

    public ExceptionResponse getAveragePassengerRatingWhenPassengerNotExistsRequest(int port, String invalidId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL + "/average")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }
}
