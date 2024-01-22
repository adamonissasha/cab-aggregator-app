package com.example.driverservice.integration;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.dto.response.ValidationErrorResponse;
import com.example.driverservice.model.enums.Status;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.util.TestDriverUtil;
import com.example.driverservice.util.client.DriverClientUtil;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:sql/add-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/delete-test-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Testcontainers
public class DriverControllerTest {
    @LocalServerPort
    private int port;

    private final DriverRepository driverRepository;

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

    @Autowired
    public DriverControllerTest(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Test
    void createDriver_WhenPhoneNumberUniqueAndDataValid_ShouldReturnDriverResponse() {
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();
        DriverResponse expected = TestDriverUtil.getDriverResponse();

        DriverResponse actual =
                DriverClientUtil.createDriverWhenPhoneNumberUniqueAndDataValidRequest(port, driverRequest);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);

        deleteDriverAfterTest(actual.getId());
    }

    @Test
    void createDriver_WhenPhoneNumberAlreadyExists_ShouldReturnConflictResponse() {
        DriverRequest driverRequest = TestDriverUtil.getDriverRequestWithExistingNumber();
        ExceptionResponse expected = TestDriverUtil.getPhoneNumberExistsExceptionResponse();

        ExceptionResponse actual = DriverClientUtil.createDriverWhenPhoneNumberAlreadyExistsRequest(port, driverRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createDriver_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        DriverRequest driverRequest = TestDriverUtil.getDriverRequestWithInvalidData();
        ValidationErrorResponse expected = TestDriverUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = DriverClientUtil.createDriverWhenDataNotValidRequest(port, driverRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editDriver_WhenDataValid_ShouldReturnDriverResponse() {
        Long driverId = TestDriverUtil.getSecondDriverId();
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();
        DriverResponse expected = TestDriverUtil.getDriverResponse();
        expected.setId(driverId);
        expected.setRating(TestDriverUtil.getSecondDriverRating().getAverageRating());

        DriverResponse actual = DriverClientUtil.editDriverWhenDataValidRequest(port, driverRequest, driverId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editDriver_WhenInvalidData_ShouldReturnBadRequestResponse() {
        Long driverId = TestDriverUtil.getFirstDriverId();
        DriverRequest driverRequest = TestDriverUtil.getDriverRequestWithInvalidData();
        ValidationErrorResponse expected = TestDriverUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = DriverClientUtil.editDriverWhenInvalidDataRequest(port, driverRequest, driverId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editDriver_WhenDriverNotFound_ShouldReturnNotFoundResponse() {
        Long invalidDriverId = TestDriverUtil.getInvalidId();
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();
        ExceptionResponse expected = TestDriverUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual =
                DriverClientUtil.editDriverWhenDriverNotFoundRequest(port, driverRequest, invalidDriverId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getDriverById_WhenDriverExists_ShouldReturnDriverResponse() {
        Long existingDriverId = TestDriverUtil.getSecondDriverId();
        DriverResponse expected = TestDriverUtil.getSecondDriverResponse();

        DriverResponse actual = DriverClientUtil.getDriverByIdWhenDriverExistsRequest(port, existingDriverId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getDriverById_WhenDriverNotExists_ShouldReturnNotFoundResponse() {
        Long invalidDriverId = TestDriverUtil.getInvalidId();
        ExceptionResponse expected = TestDriverUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual = DriverClientUtil.getDriverByIdWhenDriverNotExistsRequest(port, invalidDriverId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllDrivers_ShouldReturnDriverPageResponse() {
        int page = TestDriverUtil.getPageNumber();
        int size = TestDriverUtil.getPageSize();
        String sortBy = TestDriverUtil.getCorrectSortField();
        DriverPageResponse expected = TestDriverUtil.getDriverPageResponse();

        DriverPageResponse actual = DriverClientUtil.getAllDriversRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllDrivers_WhenIncorrectField_ShouldReturnExceptionResponse() {
        int page = TestDriverUtil.getPageNumber();
        int size = TestDriverUtil.getPageSize();
        String sortBy = TestDriverUtil.getIncorrectSortField();
        ExceptionResponse expected = TestDriverUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual = DriverClientUtil.getAllDriversWhenIncorrectFieldRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteDriver_WhenDriverExists_ShouldReturnNotFoundAfterDeletion() {
        Long existingDriverId = TestDriverUtil.getSecondDriverId();

        DriverClientUtil.deleteDriverWhenDriverExistsRequest(port, existingDriverId);
    }

    @Test
    void deleteDriver_WhenDriverNotExists_ShouldReturnNotFound() {
        Long invalidDriverId = TestDriverUtil.getInvalidId();

        DriverClientUtil.deleteDriverWhenDriverNotExistsRequest(port, invalidDriverId);
    }


    @Test
    void changeStatusToFree_WhenChangeStatusBusy_ShouldReturnFreeDriverResponse() {
        Long driverId = TestDriverUtil.getThirdDriverId();
        DriverResponse expected = TestDriverUtil.getThirdDriverResponse();
        expected.setStatus(Status.FREE.name());

        DriverResponse actual = DriverClientUtil.changeStatusToFreeWhenChangeStatusBusyRequest(port, driverId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void changeStatusToFree_WhenDriverNotFound_ShouldReturnNotFoundResponse() {
        Long invalidDriverId = TestDriverUtil.getInvalidId();
        ExceptionResponse expected = TestDriverUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual = DriverClientUtil.changeStatusToFreeWhenDriverNotFoundRequest(port, invalidDriverId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void changeStatusToFree_WhenStatusAlreadyFree_ShouldReturnConflictResponse() {
        Long driverId = TestDriverUtil.getSecondDriverId();
        ExceptionResponse expected = TestDriverUtil.getStatusExceptionResponse();

        ExceptionResponse actual = DriverClientUtil.changeStatusToFreeWhenStatusAlreadyFreeRequest(port, driverId);

        assertThat(actual).isEqualTo(expected);
    }

    void deleteDriverAfterTest(Long id) {
        driverRepository.deleteById(id);
    }
}