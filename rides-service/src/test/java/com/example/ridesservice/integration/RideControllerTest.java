package com.example.ridesservice.integration;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.request.RatingRequest;
import com.example.ridesservice.dto.response.ExceptionResponse;
import com.example.ridesservice.dto.response.PassengerRideResponse;
import com.example.ridesservice.dto.response.PassengerRidesPageResponse;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.dto.response.RidesPageResponse;
import com.example.ridesservice.dto.response.ValidationErrorResponse;
import com.example.ridesservice.repository.RideRepository;
import com.example.ridesservice.repository.StopRepository;
import com.example.ridesservice.util.TestRideUtil;
import com.example.ridesservice.util.client.RideClientUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:sql/add-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/delete-test-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Testcontainers
public class RideControllerTest {
    @LocalServerPort
    private int port;

    private final RideRepository rideRepository;

    private final StopRepository stopRepository;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Container
    static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        dynamicPropertyRegistry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    public RideControllerTest(RideRepository rideRepository, StopRepository stopRepository) {
        this.rideRepository = rideRepository;
        this.stopRepository = stopRepository;
    }

    @Test
    void createRide_WhenDataValid_ShouldReturnRideResponse() {
        CreateRideRequest createRideRequest = TestRideUtil.getCreateRideRequest();
        PassengerRideResponse expected = TestRideUtil.getPassengerRideResponse();

        PassengerRideResponse actual = RideClientUtil.createRideWhenDataValidRequest(port, createRideRequest);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("rideId", "driverName", "driverPhoneNumber", "stops",
                        "driverRating", "carColor", "carMake", "carNumber", "creationDateTime", "price")
                .isEqualTo(expected);

        assertThat(actual.getCreationDateTime())
                .isCloseTo(expected.getCreationDateTime(), within(10, SECONDS));

        deleteRideAfterTest(actual.getRideId(), actual.getStops().get(0).getId());
    }

    @Test
    void createRide_WhenPassengerRideAlreadyExists_ShouldReturnConflictResponse() {
        CreateRideRequest createRideRequest = TestRideUtil.getCreateExistingRideRequest();
        ExceptionResponse expected = TestRideUtil.getPassengerExceptionResponse();

        ExceptionResponse actual =
                RideClientUtil.createRideWhenPassengerRideAlreadyExistsRequest(port, createRideRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createRide_WhenIncorrectPaymentMethod_ShouldReturnConflictResponse() {
        CreateRideRequest createRideRequest = TestRideUtil.getCreateRideRequestWithIncorrectPaymentMethod();
        ExceptionResponse expected = TestRideUtil.getPaymentMethodExceptionResponse();

        ExceptionResponse actual =
                RideClientUtil.createRideWhenIncorrectPaymentMethodRequest(port, createRideRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createRide_WhenDataInvalid_ShouldReturnBadRequestResponse() {
        CreateRideRequest createRideRequest = TestRideUtil.getCreateRideRequestWithInvalidData();
        ValidationErrorResponse expected = TestRideUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = RideClientUtil.createRideWhenDataInvalidRequest(port, createRideRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editRide_WhenValidData_ShouldReturnRideResponse() {
        Long rideId = TestRideUtil.getFirstRideId();
        EditRideRequest editRideRequest = TestRideUtil.getEditRideRequest();
        PassengerRideResponse expected = TestRideUtil.getEditedPassengerRideResponse();

        PassengerRideResponse actual = RideClientUtil.editRideWhenValidDataRequest(port, rideId, editRideRequest);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("creationDateTime", "price")
                .isEqualTo(expected);

        assertThat(actual.getCreationDateTime())
                .isCloseTo(expected.getCreationDateTime(), within(10, SECONDS));
    }

    @Test
    void editRide_WhenInvalidData_ShouldReturnBadRequestResponse() {
        Long rideId = TestRideUtil.getFirstRideId();
        EditRideRequest editRideRequest = TestRideUtil.getEditRideRequestWithInvalidData();
        ValidationErrorResponse expected = TestRideUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = RideClientUtil.editRideWhenInvalidDataRequest(port, editRideRequest, rideId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editRide_WhenRideNotFound_ShouldReturnNotFoundResponse() {
        Long invalidRideId = TestRideUtil.getInvalidRideId();
        EditRideRequest editRideRequest = TestRideUtil.getEditRideRequest();
        ExceptionResponse expected = TestRideUtil.getRideNotFoundExceptionResponse();

        ExceptionResponse actual =
                RideClientUtil.editRideWhenRideNotFoundRequest(port, editRideRequest, invalidRideId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editRide_WhenStatusCanceled_ShouldReturnBadRequestResponse() {
        Long rideId = TestRideUtil.getSecondRideId();
        EditRideRequest editRideRequest = TestRideUtil.getEditRideRequest();
        ExceptionResponse expected = TestRideUtil.getStatusExceptionResponse();

        ExceptionResponse actual = RideClientUtil.editRideWhenStatusCanceledRequest(port, rideId, editRideRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void startRide_ShouldReturnRideResponse() {
        Long rideId = TestRideUtil.getFirstRideId();
        RideResponse expected = TestRideUtil.getStartedRideResponse();

        RideResponse actual = RideClientUtil.startRideRequest(port, rideId);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("creationDateTime", "startDateTime")
                .isEqualTo(expected);

        assertThat(actual.getCreationDateTime())
                .isCloseTo(expected.getCreationDateTime(), within(10, SECONDS));

        assertThat(actual.getStartDateTime())
                .isCloseTo(expected.getStartDateTime(), within(10, SECONDS));
    }

    @Test
    void startRide_WhenStatusCanceled_ShouldReturnBadRequestResponse() {
        Long rideId = TestRideUtil.getSecondRideId();
        ExceptionResponse expected = TestRideUtil.getStatusExceptionResponse();

        ExceptionResponse actual = RideClientUtil.startRideWhenStatusCanceledRequest(port, rideId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void startRide_WhenRideNotFound_ShouldReturnNotFoundResponse() {
        Long invalidRideId = TestRideUtil.getInvalidRideId();
        ExceptionResponse expected = TestRideUtil.getRideNotFoundExceptionResponse();

        ExceptionResponse actual = RideClientUtil.startRideWhenRideNotFoundRequest(port, invalidRideId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void cancelRide_ShouldReturnRideResponse() {
        Long rideId = TestRideUtil.getFirstRideId();
        RideResponse expected = TestRideUtil.getCanceledRideResponse();

        RideResponse actual = RideClientUtil.cancelRideRequest(port, rideId);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("creationDateTime")
                .isEqualTo(expected);

        assertThat(actual.getCreationDateTime())
                .isCloseTo(expected.getCreationDateTime(), within(10, SECONDS));
    }

    @Test
    void cancelRide_WhenStatusCanceled_ShouldReturnBadRequestResponse() {
        Long rideId = TestRideUtil.getSecondRideId();
        ExceptionResponse expected = TestRideUtil.getStatusExceptionResponse();

        ExceptionResponse actual = RideClientUtil.cancelRideWhenStatusCanceledRequest(port, rideId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void cancelRide_WhenRideNotFound_ShouldReturnNotFoundResponse() {
        Long invalidRideId = TestRideUtil.getInvalidRideId();
        ExceptionResponse expected = TestRideUtil.getRideNotFoundExceptionResponse();

        ExceptionResponse actual = RideClientUtil.cancelRideWhenRideNotFoundRequest(port, invalidRideId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void completeRide_ShouldReturnRideResponse() {
        Long rideId = TestRideUtil.getThirdRideId();
        RideResponse expected = TestRideUtil.getCompletedRideResponse();

        RideResponse actual = RideClientUtil.completeRideRequest(port, rideId);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("creationDateTime", "startDateTime", "endDateTime")
                .isEqualTo(expected);

        assertThat(actual.getCreationDateTime())
                .isCloseTo(expected.getCreationDateTime(), within(5, SECONDS));

        assertThat(actual.getStartDateTime())
                .isCloseTo(expected.getStartDateTime(), within(5, SECONDS));

        assertThat(actual.getEndDateTime())
                .isCloseTo(expected.getEndDateTime(), within(5, SECONDS));
    }

    @Test
    void completeRide_WhenStatusCanceled_ShouldReturnBadRequestResponse() {
        Long rideId = TestRideUtil.getSecondRideId();
        ExceptionResponse expected = TestRideUtil.getStatusExceptionResponse();

        ExceptionResponse actual = RideClientUtil.completeRideWhenStatusCanceledRequest(port, rideId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void completeRide_WhenRideNotFound_ShouldReturnNotFoundResponse() {
        Long invalidRideId = TestRideUtil.getInvalidRideId();
        ExceptionResponse expected = TestRideUtil.getRideNotFoundExceptionResponse();

        ExceptionResponse actual =
                RideClientUtil.completeRideWhenRideNotFoundRequest(port, invalidRideId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRideByRideId_WhenRideExists_ShouldReturnRideResponse() {
        Long existingRideId = TestRideUtil.getFirstRideId();
        RideResponse expected = TestRideUtil.getFirstRideResponse();

        RideResponse actual = RideClientUtil.getRideByRideIdWhenRideExistsRequest(port, existingRideId);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("creationDateTime")
                .isEqualTo(expected);

        assertThat(actual.getCreationDateTime())
                .isCloseTo(expected.getCreationDateTime(), within(5, SECONDS));
    }

    @Test
    void getRideById_WhenRideNotExists_ShouldReturnNotFoundResponse() {
        Long invalidRideId = TestRideUtil.getInvalidRideId();
        ExceptionResponse expected = TestRideUtil.getRideNotFoundExceptionResponse();

        ExceptionResponse actual = RideClientUtil.getRideByRideIdWhenRideNotExistsRequest(port, invalidRideId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllPassengerRides_ShouldReturnPassengerRidesPageResponse() {
        int page = TestRideUtil.getPageNumber();
        int size = TestRideUtil.getPageSize();
        String sortBy = TestRideUtil.getCorrectSortField();
        Long passengerId = TestRideUtil.getFirstPassengerId();
        PassengerRidesPageResponse expected = TestRideUtil.getPassengerRidesPageResponse();

        PassengerRidesPageResponse actual =
                RideClientUtil.getAllPassengerRidesRequest(port, page, size, sortBy, passengerId);

        assertThat(actual.getRides())
                .usingElementComparator(
                        (a, e) ->
                                a.getCreationDateTime().isEqual(e.getCreationDateTime())
                                        ? a.getCreationDateTime().compareTo(e.getCreationDateTime())
                                        : 0
                )
                .containsExactlyInAnyOrderElementsOf(expected.getRides());
    }

    @Test
    void getAllPassengerRides_WhenIncorrectField_ShouldReturnExceptionResponse() {
        int page = TestRideUtil.getPageNumber();
        int size = TestRideUtil.getPageSize();
        String sortBy = TestRideUtil.getIncorrectSortField();
        Long passengerId = TestRideUtil.getFirstPassengerId();
        ExceptionResponse expected = TestRideUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual =
                RideClientUtil.getAllPassengerRidesWhenIncorrectFieldRequest(port, page, size, sortBy, passengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllDriverRides_ShouldReturnRidesPageResponse() {
        int page = TestRideUtil.getPageNumber();
        int size = TestRideUtil.getPageSize();
        String sortBy = TestRideUtil.getCorrectSortField();
        Long driverId = TestRideUtil.getFirstDriverId();
        RidesPageResponse expected = TestRideUtil.getDriverRidesPageResponse();

        RidesPageResponse actual =
                RideClientUtil.getAllDriverRidesRequest(port, page, size, sortBy, driverId);

        assertThat(actual.getRides())
                .usingElementComparator(
                        (a, e) ->
                                a.getCreationDateTime().isEqual(e.getCreationDateTime())
                                        ? a.getCreationDateTime().compareTo(e.getCreationDateTime())
                                        : 0
                )
                .containsExactlyInAnyOrderElementsOf(expected.getRides());
    }

    @Test
    void getAllDriverRides_WhenIncorrectField_ShouldReturnExceptionResponse() {
        int page = TestRideUtil.getPageNumber();
        int size = TestRideUtil.getPageSize();
        String sortBy = TestRideUtil.getIncorrectSortField();
        Long driverId = TestRideUtil.getFirstDriverId();
        ExceptionResponse expected = TestRideUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual =
                RideClientUtil.getAllDriverRidesWhenIncorrectFieldRequest(port, page, size, sortBy, driverId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void ratePassenger_WhenRideExists_ShouldReturnNotFoundResponse() {
        Long rideId = TestRideUtil.getFourthRideId();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();

        RideClientUtil.ratePassengerWhenRideExistsRequest(port, rideId, ratingRequest);
    }

    @Test
    void ratePassenger_WhenRideNotFound_ShouldReturnNotFoundResponse() {
        Long invalidRideId = TestRideUtil.getInvalidRideId();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();
        ExceptionResponse expected = TestRideUtil.getRideNotFoundExceptionResponse();

        ExceptionResponse actual =
                RideClientUtil.ratePassengerWhenRideNotFoundRequest(port, invalidRideId, ratingRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void rateDriver_WhenRideExists_ShouldReturnNotFoundResponse() {
        Long rideId = TestRideUtil.getFourthRideId();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();

        RideClientUtil.rateDriverWhenRideExistsRequest(port, rideId, ratingRequest);
    }

    @Test
    void rateDriver_WhenRideNotFound_ShouldReturnNotFoundResponse() {
        Long invalidRideId = TestRideUtil.getInvalidRideId();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();
        ExceptionResponse expected = TestRideUtil.getRideNotFoundExceptionResponse();

        ExceptionResponse actual = RideClientUtil.rateDriverWhenRideNotFoundRequest(port, invalidRideId, ratingRequest);

        assertThat(actual).isEqualTo(expected);
    }

    void deleteRideAfterTest(Long rideId, Long stopId) {
        stopRepository.deleteById(stopId);
        rideRepository.deleteById(rideId);
    }
}
