package com.example.driverservice.util.client;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarPageResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.dto.response.ValidationErrorResponse;
import io.restassured.http.ContentType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CarClientUtil {
    static String CAR_SERVICE_URL = "driver/car";
    static String ID_PARAMETER_NAME = "id";
    static String PAGE_PARAMETER_NAME = "page";
    static String SIZE_PARAMETER_NAME = "size";
    static String SORT_PARAMETER_NAME = "sortBy";

    public static CarResponse createCarWithUniqueCarNumberAndValidDataRequest(int port, CarRequest carRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .when()
                .post(CAR_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(CarResponse.class);
    }

    public static ExceptionResponse createCarWithExistingCarNumberRequest(int port, CarRequest carRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .when()
                .post(CAR_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static ValidationErrorResponse createCarWithInvalidDataRequest(int port, CarRequest carRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .when()
                .post(CAR_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public static CarResponse editCarWithValidDataRequest(int port, CarRequest carRequest, Long carId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .pathParam(ID_PARAMETER_NAME, carId)
                .when()
                .put(CAR_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CarResponse.class);
    }

    public static ValidationErrorResponse editCarWithInvalidDataRequest(int port, CarRequest carRequest, Long carId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .pathParam(ID_PARAMETER_NAME, carId)
                .when()
                .put(CAR_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public static ExceptionResponse editCarWhenCarNotFoundRequest(int port, CarRequest carRequest, Long invalidCarId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .pathParam(ID_PARAMETER_NAME, invalidCarId)
                .when()
                .put(CAR_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static CarResponse getCarByIdRequest(int port, Long existingCarId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingCarId)
                .when()
                .get(CAR_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CarResponse.class);
    }

    public static ExceptionResponse getCarByIdWhenCarNotExistsRequest(int port, Long invalidCarId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidCarId)
                .when()
                .get(CAR_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static CarPageResponse getAllCarsRequest(int port, int page, int size, String sortBy) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(CAR_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CarPageResponse.class);
    }

    public static ExceptionResponse getAllCarsWhenIncorrectFieldRequest(int port, int page, int size, String sortBy) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(CAR_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }
}