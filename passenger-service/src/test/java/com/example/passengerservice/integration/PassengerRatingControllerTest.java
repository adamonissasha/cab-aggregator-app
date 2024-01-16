package com.example.passengerservice.integration;

import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.integration.client.PassengerRatingClientTest;
import com.example.passengerservice.util.TestPassengerRatingUtil;
import com.example.passengerservice.util.TestPassengerUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:sql/add-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/delete-test-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Testcontainers
public class PassengerRatingControllerTest {
    @LocalServerPort
    private int port;

    private final PassengerRatingClientTest passengerRatingClientTest;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    public PassengerRatingControllerTest(PassengerRatingClientTest passengerRatingClientTest) {
        this.passengerRatingClientTest = passengerRatingClientTest;
    }

    @Test
    void getAllPassengerRatings_WhenPassengerExists_ShouldReturnPassengerResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        AllPassengerRatingsResponse expected = TestPassengerRatingUtil.getAllPassengerRatingsResponse();

        AllPassengerRatingsResponse actual =
                passengerRatingClientTest.getAllPassengerRatingsWhenPassengerExistsRequest(port, passengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerRatings_WhenPassengerNotExists_ShouldReturnNotFoundResponse() {
        Long invalidId = TestPassengerUtil.getInvalidId();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual =
                passengerRatingClientTest.getPassengerRatingsWhenPassengerNotExistsRequest(port, invalidId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAveragePassengerRating_WhenPassengerExists_ShouldReturnAveragePassengerRatingResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        AveragePassengerRatingResponse expected = TestPassengerRatingUtil.getAveragePassengerRatingResponse();

        AveragePassengerRatingResponse actual =
                passengerRatingClientTest.getAveragePassengerRatingWhenPassengerExistsRequest(port, passengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAveragePassengerRating_WhenPassengerNotExists_ShouldReturnNotFoundResponse() {
        Long invalidId = TestPassengerUtil.getInvalidId();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual =
                passengerRatingClientTest.getAveragePassengerRatingWhenPassengerNotExistsRequest(port, invalidId);

        assertThat(actual).isEqualTo(expected);
    }
}
