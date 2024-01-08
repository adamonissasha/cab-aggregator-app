package com.example.passengerservice.integration;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.ValidationErrorResponse;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.util.TestPassengerUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PassengerControllerTest {
    @LocalServerPort
    private int port;
    private final PassengerRepository passengerRepository;

    private static final String PASSENGER_SERVICE_URL = "passenger";

    @Autowired
    public PassengerControllerTest(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Test
    void createPassenger_WhenPhoneNumberUniqueAndDataValid_ShouldReturnPassengerResponse() {
        PassengerRequest passengerRequest = TestPassengerUtil.getUniquePassengerRequest();
        PassengerResponse expected = TestPassengerUtil.getNewPassengerResponse();

        PassengerResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .when()
                .post(PASSENGER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(PassengerResponse.class);

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

        ExceptionResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .when()
                .post(PASSENGER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createPassenger_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequestWithInvalidData();
        ValidationErrorResponse expected = TestPassengerUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .when()
                .post(PASSENGER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPassenger_WhenValidData_ShouldReturnPassengerResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        PassengerResponse expected = TestPassengerUtil.getPassengerResponse();

        PassengerResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .pathParam("id", passengerId)
                .when()
                .put(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPassenger_WhenInvalidData_ShouldReturnBadRequestResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequestWithInvalidData();
        ValidationErrorResponse expected = TestPassengerUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .pathParam("id", passengerId)
                .when()
                .put(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editPassenger_WhenPassengerNotFound_ShouldReturnNotFoundResponse() {
        Long invalidPassengerId = TestPassengerUtil.getInvalidId();
        PassengerRequest passengerRequest = TestPassengerUtil.getPassengerRequest();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .pathParam("id", invalidPassengerId)
                .when()
                .put(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerById_WhenPassengerExists_ShouldReturnPassengerResponse() {
        Long existingPassengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerResponse expected = TestPassengerUtil.getPassengerResponse();

        PassengerResponse actual = given()
                .port(port)
                .pathParam("id", existingPassengerId)
                .when()
                .get(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerById_WhenPassengerNotExists_ShouldReturnNotFoundResponse() {
        Long invalidPassengerId = TestPassengerUtil.getInvalidId();
        ExceptionResponse expected = TestPassengerUtil.getPassengerNotFoundExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .pathParam("id", invalidPassengerId)
                .when()
                .get(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllPassengers_ShouldReturnPassengerPageResponse() {
        int page = TestPassengerUtil.getPageNumber();
        int size = TestPassengerUtil.getPageSize();
        String sortBy = TestPassengerUtil.getSortField();
        PassengerPageResponse expected = TestPassengerUtil.getPassengerPageResponse();

        PassengerPageResponse actual = given()
                .port(port)
                .param("page", page)
                .param("size", size)
                .param("sortBy", sortBy)
                .when()
                .get(PASSENGER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerPageResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deletePassenger_WhenPassengerExists_ShouldReturnNotFoundAfterDeletion() {
        Long existingPassengerId = TestPassengerUtil.getFirstPassengerId();

        given()
                .port(port)
                .pathParam("id", existingPassengerId)
                .when()
                .delete(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void deletePassenger_WhenPassengerNotExists_ShouldReturnNotFound() {
        Long invalidPassengerId = TestPassengerUtil.getInvalidId();

        given()
                .port(port)
                .pathParam("id", invalidPassengerId)
                .when()
                .delete(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    void deletePassengerAfterTest(Long id) {
        passengerRepository.deleteById(id);
    }
}
