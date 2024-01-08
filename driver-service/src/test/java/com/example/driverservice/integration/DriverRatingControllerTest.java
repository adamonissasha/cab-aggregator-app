package com.example.driverservice.integration;

import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.util.TestDriverRatingUtil;
import com.example.driverservice.util.TestDriverUtil;
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
public class DriverRatingControllerTest {
    @LocalServerPort
    private int port;

    private static final String DRIVER_RATING_SERVICE_URL = "driver/{id}/rating";

    @Test
    void getAllDriverRatings_WhenDriverExists_ShouldReturnDriverResponse() {
        Long driverId = TestDriverUtil.getSecondDriverId();
        AllDriverRatingsResponse expected = TestDriverRatingUtil.getAllDriverRatingsResponse();

        AllDriverRatingsResponse actual = given()
                .port(port)
                .pathParam("id", driverId)
                .when()
                .get(DRIVER_RATING_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AllDriverRatingsResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getDriverRatings_WhenDriverNotExists_ShouldReturnNotFoundResponse() {
        Long invalidId = TestDriverUtil.getInvalidId();
        ExceptionResponse expected = TestDriverUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .pathParam("id", invalidId)
                .when()
                .get(DRIVER_RATING_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAverageDriverRating_WhenDriverExists_ShouldReturnAverageDriverRatingResponse() {
        Long driverId = TestDriverUtil.getSecondDriverId();
        AverageDriverRatingResponse expected = TestDriverRatingUtil.getAverageDriverRatingResponse();

        AverageDriverRatingResponse actual = given()
                .port(port)
                .pathParam("id", driverId)
                .when()
                .get(DRIVER_RATING_SERVICE_URL + "/average")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AverageDriverRatingResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAverageDriverRating_WhenDriverNotExists_ShouldReturnNotFoundResponse() {
        Long invalidId = TestDriverUtil.getInvalidId();
        ExceptionResponse expected = TestDriverUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .pathParam("id", invalidId)
                .when()
                .get(DRIVER_RATING_SERVICE_URL + "/average")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }
}
