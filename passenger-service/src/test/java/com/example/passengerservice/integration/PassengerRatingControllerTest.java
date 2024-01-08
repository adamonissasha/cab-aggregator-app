package com.example.passengerservice.integration;

import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.util.TestPassengerRatingUtil;
import com.example.passengerservice.util.TestPassengerUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:sql/add-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/delete-test-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PassengerRatingControllerTest {
    @LocalServerPort
    private int port;

    private static final String PASSENGER_RATING_SERVICE_URL = "passenger/{id}/rating";

    @Test
    void getAllPassengerRatings_WhenPassengerExists_ShouldReturnPassengerResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        AllPassengerRatingsResponse expected = TestPassengerRatingUtil.getAllPassengerRatingsResponse();

        AllPassengerRatingsResponse actual = given()
                .port(port)
                .pathParam("id", passengerId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AllPassengerRatingsResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerRatings_WhenPassengerNotExists_ShouldReturnNotFoundResponse() {
        Long invalidId = TestPassengerUtil.getInvalidId();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .pathParam("id", invalidId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAveragePassengerRating_WhenPassengerExists_ShouldReturnAveragePassengerRatingResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        AveragePassengerRatingResponse expected = TestPassengerRatingUtil.getAveragePassengerRatingResponse();

        AveragePassengerRatingResponse actual = given()
                .port(port)
                .pathParam("id", passengerId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL + "/average")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AveragePassengerRatingResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAveragePassengerRating_WhenPassengerNotExists_ShouldReturnNotFoundResponse() {
        Long invalidId = TestPassengerUtil.getInvalidId();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .pathParam("id", invalidId)
                .when()
                .get(PASSENGER_RATING_SERVICE_URL + "/average")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }
}
