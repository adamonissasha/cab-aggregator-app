package com.example.driverservice.integration;

import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.util.TestDriverRatingUtil;
import com.example.driverservice.util.TestDriverUtil;
import com.example.driverservice.util.client.DriverRatingClientUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:sql/add-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/delete-test-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Testcontainers
public class DriverRatingControllerTest {
    @LocalServerPort
    private int port;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        dynamicPropertyRegistry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    void getAllDriverRatings_WhenDriverExists_ShouldReturnDriverResponse() {
        Long driverId = TestDriverUtil.getSecondDriverId();
        AllDriverRatingsResponse expected = TestDriverRatingUtil.getAllDriverRatingsResponse();

        AllDriverRatingsResponse actual
                = DriverRatingClientUtil.getAllDriverRatingsWhenDriverExistsRequest(port, driverId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getDriverRatings_WhenDriverNotExists_ShouldReturnNotFoundResponse() {
        Long invalidId = TestDriverUtil.getInvalidId();
        ExceptionResponse expected = TestDriverUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual =
                DriverRatingClientUtil.getDriverRatingsWhenDriverNotExistsRequest(port, invalidId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAverageDriverRating_WhenDriverExists_ShouldReturnAverageDriverRatingResponse() {
        Long driverId = TestDriverUtil.getSecondDriverId();
        AverageDriverRatingResponse expected = TestDriverRatingUtil.getAverageDriverRatingResponse();

        AverageDriverRatingResponse actual =
                DriverRatingClientUtil.getAverageDriverRatingWhenDriverExistsRequest(port, driverId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAverageDriverRating_WhenDriverNotExists_ShouldReturnNotFoundResponse() {
        Long invalidId = TestDriverUtil.getInvalidId();
        ExceptionResponse expected = TestDriverUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual =
                DriverRatingClientUtil.getAverageDriverRatingWhenDriverNotExistsRequest(port, invalidId);

        assertThat(actual).isEqualTo(expected);
    }
}