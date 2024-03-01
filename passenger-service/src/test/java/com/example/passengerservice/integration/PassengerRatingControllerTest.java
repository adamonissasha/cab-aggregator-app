package com.example.passengerservice.integration;

import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.sql.InitDb;
import com.example.passengerservice.util.TestPassengerRatingUtil;
import com.example.passengerservice.util.TestPassengerUtil;
import com.example.passengerservice.util.client.PassengerRatingClientUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class PassengerRatingControllerTest {
    @LocalServerPort
    private int port;
    private final InitDb initDb;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
    );

    @Autowired
    public PassengerRatingControllerTest(InitDb initDb) {
        this.initDb = initDb;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    public void setUp() {
        initDb.addTestData();
    }

    @AfterEach
    public void tearDown() {
        initDb.deleteTestData();
    }

    @Test
    void getAllPassengerRatings_WhenPassengerExists_ShouldReturnPassengerResponse() {
        String passengerId = TestPassengerUtil.getSecondPassengerId();
        AllPassengerRatingsResponse expected = TestPassengerRatingUtil.getAllPassengerRatingsResponse();

        AllPassengerRatingsResponse actual =
                PassengerRatingClientUtil.getAllPassengerRatingsWhenPassengerExistsRequest(port, passengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerRatings_WhenPassengerNotExists_ShouldReturnNotFoundResponse() {
        String invalidId = TestPassengerUtil.getInvalidId();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual =
                PassengerRatingClientUtil.getPassengerRatingsWhenPassengerNotExistsRequest(port, invalidId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAveragePassengerRating_WhenPassengerExists_ShouldReturnAveragePassengerRatingResponse() {
        String passengerId = TestPassengerUtil.getSecondPassengerId();
        AveragePassengerRatingResponse expected = TestPassengerRatingUtil.getAveragePassengerRatingResponse();

        AveragePassengerRatingResponse actual =
                PassengerRatingClientUtil.getAveragePassengerRatingWhenPassengerExistsRequest(port, passengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAveragePassengerRating_WhenPassengerNotExists_ShouldReturnNotFoundResponse() {
        String invalidId = TestPassengerUtil.getInvalidId();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual =
                PassengerRatingClientUtil.getAveragePassengerRatingWhenPassengerNotExistsRequest(port, invalidId);

        assertThat(actual).isEqualTo(expected);
    }
}