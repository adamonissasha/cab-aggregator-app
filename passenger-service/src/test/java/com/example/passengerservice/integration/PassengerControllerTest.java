package com.example.passengerservice.integration;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.ValidationErrorResponse;
import com.example.passengerservice.integration.client.PassengerClientTest;
import com.example.passengerservice.repository.PassengerRepository;
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
public class PassengerControllerTest {
    @LocalServerPort
    private int port;

    private final PassengerRepository passengerRepository;

    private final PassengerClientTest passengerClientTest;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    public PassengerControllerTest(PassengerRepository passengerRepository, PassengerClientTest passengerClientTest) {
        this.passengerRepository = passengerRepository;
        this.passengerClientTest = passengerClientTest;
    }

    @Test
    void createPassenger_WhenPhoneNumberUniqueAndDataValid_ShouldReturnPassengerResponse() {
        PassengerRequest passengerRequest = TestPassengerUtil.getUniquePassengerRequest();
        PassengerResponse expected = TestPassengerUtil.getNewPassengerResponse();

        PassengerResponse actual = passengerClientTest.createPassengerWhenDataValidRequest(port, passengerRequest);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);

        deletePassengerAfterTest(actual.getId());
    }

    @Test
    void createPassenger_WhenPhoneNumberAlreadyExists_ShouldReturnConflictResponse() {
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequestWithExistingNumber();
        ExceptionResponse expected = TestPassengerUtil.getPhoneNumberExistsExceptionResponse();

        ExceptionResponse actual =
                passengerClientTest.createPassengerWhenPhoneNumberAlreadyExistsRequest(port, passengerRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createPassenger_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequestWithInvalidData();
        ValidationErrorResponse expected = TestPassengerUtil.getValidationErrorResponse();

        ValidationErrorResponse actual =
                passengerClientTest.createPassengerWhenDataNotValidRequest(port, passengerRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPassenger_WhenValidData_ShouldReturnPassengerResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        PassengerResponse expected = TestPassengerUtil.getPassengerResponse();

        PassengerResponse actual =
                passengerClientTest.editPassengerWhenValidDataRequest(port, passengerRequest, passengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPassenger_WhenInvalidData_ShouldReturnBadRequestResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequestWithInvalidData();
        ValidationErrorResponse expected = TestPassengerUtil.getValidationErrorResponse();

        ValidationErrorResponse actual =
                passengerClientTest.editPassengerWhenInvalidDataRequest(port, passengerRequest, passengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPassenger_WhenPassengerNotFound_ShouldReturnNotFoundResponse() {
        Long invalidPassengerId = TestPassengerUtil.getInvalidId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual =
                passengerClientTest.editPassengerWhenPassengerNotFoundRequest(port, passengerRequest, invalidPassengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerById_WhenPassengerExists_ShouldReturnPassengerResponse() {
        Long existingPassengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerResponse expected = TestPassengerUtil.getPassengerResponse();

        PassengerResponse actual =
                passengerClientTest.getPassengerByIdWhenPassengerExistsRequest(port, existingPassengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerById_WhenPassengerNotExists_ShouldReturnNotFoundResponse() {
        Long invalidPassengerId = TestPassengerUtil.getInvalidId();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual =
                passengerClientTest.getPassengerByIdWhenPassengerNotExistsRequest(port, invalidPassengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllPassengers_ShouldReturnPassengerPageResponse() {
        int page = TestPassengerUtil.getPageNumber();
        int size = TestPassengerUtil.getPageSize();
        String sortBy = TestPassengerUtil.getCorrectSortField();
        PassengerPageResponse expected = TestPassengerUtil.getPassengerPageResponse();

        PassengerPageResponse actual = passengerClientTest.getAllPassengersRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllPassengers_WhenIncorrectField_ShouldReturnExceptionResponse() {
        int page = TestPassengerUtil.getPageNumber();
        int size = TestPassengerUtil.getPageSize();
        String sortBy = TestPassengerUtil.getIncorrectSortField();
        ExceptionResponse expected = TestPassengerUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual =
                passengerClientTest.getAllPassengersWhenIncorrectFieldRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deletePassenger_WhenPassengerExists_ShouldReturnNotFoundAfterDeletion() {
        Long existingPassengerId = TestPassengerUtil.getFirstPassengerId();

        passengerClientTest.deletePassengerWhenPassengerExistsRequest(port, existingPassengerId);
    }

    @Test
    void deletePassenger_WhenPassengerNotExists_ShouldReturnNotFound() {
        Long invalidPassengerId = TestPassengerUtil.getInvalidId();

        passengerClientTest.deletePassengerWhenPassengerNotExistsRequest(port, invalidPassengerId);
    }

    void deletePassengerAfterTest(Long id) {
        passengerRepository.deleteById(id);
    }
}
