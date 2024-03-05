package com.example.passengerservice.integration;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.ValidationErrorResponse;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.sql.InitDb;
import com.example.passengerservice.util.TestPassengerUtil;
import com.example.passengerservice.util.client.PassengerClientUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class PassengerControllerTest {
    @LocalServerPort
    private int port;
    private final PassengerRepository passengerRepository;
    private final InitDb initDb;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    public PassengerControllerTest(PassengerRepository passengerRepository, InitDb initDb) {
        this.passengerRepository = passengerRepository;
        this.initDb = initDb;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
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
        String passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getUniquePassengerRequest();
        PassengerResponse expected = TestPassengerUtil.getPassengerResponse();
        expected.setPhoneNumber(passengerRequest.getPhoneNumber());

        PassengerResponse actual =
                PassengerClientUtil.editPassengerWhenValidDataRequest(port, passengerRequest, passengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPassenger_WhenInvalidData_ShouldReturnBadRequestResponse() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequestWithInvalidData();
        ValidationErrorResponse expected = TestPassengerUtil.getValidationErrorResponse();

        ValidationErrorResponse actual =
                PassengerClientUtil.editPassengerWhenInvalidDataRequest(port, passengerRequest, passengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPassenger_WhenPassengerNotFound_ShouldReturnNotFoundResponse() {
        String invalidPassengerId = TestPassengerUtil.getInvalidId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual =
                PassengerClientUtil.editPassengerWhenPassengerNotFoundRequest(port, passengerRequest, invalidPassengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerById_WhenPassengerExists_ShouldReturnPassengerResponse() {
        String existingPassengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerResponse expected = TestPassengerUtil.getPassengerResponse();

        PassengerResponse actual =
                PassengerClientUtil.getPassengerByIdWhenPassengerExistsRequest(port, existingPassengerId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerById_WhenPassengerNotExists_ShouldReturnNotFoundResponse() {
        String invalidPassengerId = TestPassengerUtil.getInvalidId();
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
        String existingPassengerId = TestPassengerUtil.getFirstPassengerId();

        PassengerClientUtil.deletePassengerWhenPassengerExistsRequest(port, existingPassengerId);
    }

    @Test
    void deletePassenger_WhenPassengerNotExists_ShouldReturnNotFound() {
        String invalidPassengerId = TestPassengerUtil.getInvalidId();

        PassengerClientUtil.deletePassengerWhenPassengerNotExistsRequest(port, invalidPassengerId);
    }

    void deletePassengerAfterTest(String id) {
        passengerRepository.deleteById(id).block();
    }
}
