package com.example.passengerservice.util.client;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.ValidationErrorResponse;
import io.restassured.http.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@UtilityClass
public class PassengerClientUtil {
    private final String PASSENGER_SERVICE_URL = "passenger";
    private final String ID_PARAMETER_NAME = "id";
    private final String PAGE_PARAMETER_NAME = "page";
    private final String SIZE_PARAMETER_NAME = "size";
    private final String SORT_PARAMETER_NAME = "sortBy";

    public PassengerResponse createPassengerWhenDataValidRequest(int port,
                                                                 PassengerRequest passengerRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .when()
                .post(PASSENGER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(PassengerResponse.class);
    }

    public ExceptionResponse createPassengerWhenPhoneNumberAlreadyExistsRequest(int port,
                                                                                PassengerRequest passengerRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .when()
                .post(PASSENGER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public ValidationErrorResponse createPassengerWhenDataNotValidRequest(int port,
                                                                          PassengerRequest passengerRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .when()
                .post(PASSENGER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public PassengerResponse editPassengerWhenValidDataRequest(int port,
                                                               PassengerRequest passengerRequest,
                                                               String passengerId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .pathParam(ID_PARAMETER_NAME, passengerId)
                .when()
                .put(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerResponse.class);
    }

    public ValidationErrorResponse editPassengerWhenInvalidDataRequest(int port,
                                                                       PassengerRequest passengerRequest,
                                                                       String passengerId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .pathParam(ID_PARAMETER_NAME, passengerId)
                .when()
                .put(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public ExceptionResponse editPassengerWhenPassengerNotFoundRequest(int port,
                                                                       PassengerRequest passengerRequest,
                                                                       String invalidPassengerId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .pathParam(ID_PARAMETER_NAME, invalidPassengerId)
                .when()
                .put(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public PassengerResponse getPassengerByIdWhenPassengerExistsRequest(int port, String existingPassengerId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingPassengerId)
                .when()
                .get(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerResponse.class);
    }

    public ExceptionResponse getPassengerByIdWhenPassengerNotExistsRequest(int port, String invalidPassengerId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidPassengerId)
                .when()
                .get(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public PassengerPageResponse getAllPassengersRequest(int port, int page, int size, String sortBy) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(PASSENGER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerPageResponse.class);
    }

    public ExceptionResponse getAllPassengersWhenIncorrectFieldRequest(int port,
                                                                       int page,
                                                                       int size,
                                                                       String sortBy) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(PASSENGER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public void deletePassengerWhenPassengerExistsRequest(int port, String existingPassengerId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingPassengerId)
                .when()
                .delete(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public void deletePassengerWhenPassengerNotExistsRequest(int port, String invalidPassengerId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidPassengerId)
                .when()
                .delete(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
