package com.example.passengerservice.integration.client;

import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
public class PassengerRatingClientTest {
    private static final String PASSENGER_RATING_SERVICE_URL = "passenger/{id}/rating";

    public AllPassengerRatingsResponse getAllPassengerRatingsWhenPassengerExistsRequest(int port, Long passengerId) {
        return given()
                .port(port)
                .pathParam("id", passengerId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AllPassengerRatingsResponse.class);
    }

    public ExceptionResponse getPassengerRatingsWhenPassengerNotExistsRequest(int port, Long invalidId) {
        return given()
                .port(port)
                .pathParam("id", invalidId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public AveragePassengerRatingResponse getAveragePassengerRatingWhenPassengerExistsRequest(int port, Long passengerId) {
        return given()
                .port(port)
                .pathParam("id", passengerId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL + "/average")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AveragePassengerRatingResponse.class);
    }

    public ExceptionResponse getAveragePassengerRatingWhenPassengerNotExistsRequest(int port, Long invalidId) {
        return given()
                .port(port)
                .pathParam("id", invalidId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL + "/average")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }
}
