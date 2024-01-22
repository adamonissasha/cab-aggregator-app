package com.example.passengerservice.util.client;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.ExceptionResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.ValidationErrorResponse;
import io.restassured.http.ContentType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PassengerClientUtil {
    static String PASSENGER_SERVICE_URL = "passenger";
    static String ID_PARAMETER_NAME = "id";
    static String PAGE_PARAMETER_NAME = "page";
    static String SIZE_PARAMETER_NAME = "size";
    static String SORT_PARAMETER_NAME = "sortBy";

    public static PassengerResponse createPassengerWhenDataValidRequest(int port,
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

    public static ExceptionResponse createPassengerWhenPhoneNumberAlreadyExistsRequest(int port,
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

    public static ValidationErrorResponse createPassengerWhenDataNotValidRequest(int port,
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

    public static PassengerResponse editPassengerWhenValidDataRequest(int port,
                                                                      PassengerRequest passengerRequest,
                                                                      Long passengerId) {
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

    public static ValidationErrorResponse editPassengerWhenInvalidDataRequest(int port,
                                                                              PassengerRequest passengerRequest,
                                                                              Long passengerId) {
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

    public static ExceptionResponse editPassengerWhenPassengerNotFoundRequest(int port,
                                                                              PassengerRequest passengerRequest,
                                                                              Long invalidPassengerId) {
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

    public static PassengerResponse getPassengerByIdWhenPassengerExistsRequest(int port, Long existingPassengerId) {
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

    public static ExceptionResponse getPassengerByIdWhenPassengerNotExistsRequest(int port, Long invalidPassengerId) {
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

    public static PassengerPageResponse getAllPassengersRequest(int port, int page, int size, String sortBy) {
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

    public static ExceptionResponse getAllPassengersWhenIncorrectFieldRequest(int port,
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

    public static void deletePassengerWhenPassengerExistsRequest(int port, Long existingPassengerId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingPassengerId)
                .when()
                .delete(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public static void deletePassengerWhenPassengerNotExistsRequest(int port, Long invalidPassengerId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidPassengerId)
                .when()
                .delete(PASSENGER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
