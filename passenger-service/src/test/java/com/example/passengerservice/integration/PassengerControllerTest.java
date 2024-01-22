package com.example.passengerservice.integration;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.ValidationErrorResponse;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.util.TestPassengerUtil;
import com.example.passengerservice.util.client.PassengerClientUtil;
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

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    public PassengerControllerTest(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Test
    void createPassenger_WhenPhoneNumberUniqueAndDataValid_ShouldReturnPassengerResponse() {
        PassengerRequest passengerRequest = TestPassengerUtil.getUniquePassengerRequest();
        PassengerResponse expected = TestPassengerUtil.getNewPassengerResponse();

        PassengerResponse actual = PassengerClientUtil.createPassengerWhenDataValidRequest(port, passengerRequest);

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
                PassengerClientUtil.createPassengerWhenPhoneNumberAlreadyExistsRequest(port, passengerRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createPassenger_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequestWithInvalidData();
        ValidationErrorResponse expected = TestPassengerUtil.getValidationErrorResponse();

        ValidationErrorResponse actual =
                PassengerClientUtil.createPassengerWhenDataNotValidRequest(port, passengerRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPassenger_WhenValidData_ShouldReturnPassengerResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        PassengerResponse expected = TestPassengerUtil.getPassengerResponse();

        PassengerResponse actual =
                PassengerClientUtil.editPassengerWhenValidDataRequest(port, passengerRequest, passengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPassenger_WhenInvalidData_ShouldReturnBadRequestResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequestWithInvalidData();
        ValidationErrorResponse expected = TestPassengerUtil.getValidationErrorResponse();

        ValidationErrorResponse actual =
                PassengerClientUtil.editPassengerWhenInvalidDataRequest(port, passengerRequest, passengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPassenger_WhenPassengerNotFound_ShouldReturnNotFoundResponse() {
        Long invalidPassengerId = TestPassengerUtil.getInvalidId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual =
                PassengerClientUtil.editPassengerWhenPassengerNotFoundRequest(port, passengerRequest, invalidPassengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerById_WhenPassengerExists_ShouldReturnPassengerResponse() {
        Long existingPassengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerResponse expected = TestPassengerUtil.getPassengerResponse();

        PassengerResponse actual =
                PassengerClientUtil.getPassengerByIdWhenPassengerExistsRequest(port, existingPassengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerById_WhenPassengerNotExists_ShouldReturnNotFoundResponse() {
        Long invalidPassengerId = TestPassengerUtil.getInvalidId();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual =
                PassengerClientUtil.getPassengerByIdWhenPassengerNotExistsRequest(port, invalidPassengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllPassengers_ShouldReturnPassengerPageResponse() {
        int page = TestPassengerUtil.getPageNumber();
        int size = TestPassengerUtil.getPageSize();
        String sortBy = TestPassengerUtil.getCorrectSortField();
        PassengerPageResponse expected = TestPassengerUtil.getPassengerPageResponse();

        PassengerPageResponse actual = PassengerClientUtil.getAllPassengersRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllPassengers_WhenIncorrectField_ShouldReturnExceptionResponse() {
        int page = TestPassengerUtil.getPageNumber();
        int size = TestPassengerUtil.getPageSize();
        String sortBy = TestPassengerUtil.getIncorrectSortField();
        ExceptionResponse expected = TestPassengerUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual =
                PassengerClientUtil.getAllPassengersWhenIncorrectFieldRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deletePassenger_WhenPassengerExists_ShouldReturnNotFoundAfterDeletion() {
        Long existingPassengerId = TestPassengerUtil.getFirstPassengerId();

        PassengerClientUtil.deletePassengerWhenPassengerExistsRequest(port, existingPassengerId);
    }

    @Test
    void deletePassenger_WhenPassengerNotExists_ShouldReturnNotFound() {
        Long invalidPassengerId = TestPassengerUtil.getInvalidId();

        PassengerClientUtil.deletePassengerWhenPassengerNotExistsRequest(port, invalidPassengerId);
    }

    void deletePassengerAfterTest(Long id) {
        passengerRepository.deleteById(id);
    }
}
