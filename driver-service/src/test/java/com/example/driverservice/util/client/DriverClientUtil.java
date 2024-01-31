package com.example.driverservice.util.client;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.dto.response.ValidationErrorResponse;
import io.restassured.http.ContentType;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@UtilityClass
public class DriverClientUtil {
    private final String DRIVER_SERVICE_URL = "driver";
    private final String ID_PARAMETER_NAME = "id";
    private final String PAGE_PARAMETER_NAME = "page";
    private final String SIZE_PARAMETER_NAME = "size";
    private final String SORT_PARAMETER_NAME = "sortBy";

    public DriverResponse createDriverWhenPhoneNumberUniqueAndDataValidRequest(int port, DriverRequest driverRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .when()
                .post(DRIVER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(DriverResponse.class);
    }

    public ExceptionResponse createDriverWhenPhoneNumberAlreadyExistsRequest(int port, DriverRequest driverRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .when()
                .post(DRIVER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public ValidationErrorResponse createDriverWhenDataNotValidRequest(int port, DriverRequest driverRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .when()
                .post(DRIVER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public DriverResponse editDriverWhenDataValidRequest(int port, DriverRequest driverRequest, Long driverId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .pathParam(ID_PARAMETER_NAME, driverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);
    }

    public ValidationErrorResponse editDriverWhenInvalidDataRequest(int port, DriverRequest driverRequest, Long driverId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .pathParam(ID_PARAMETER_NAME, driverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public ExceptionResponse editDriverWhenDriverNotFoundRequest(int port, DriverRequest driverRequest, Long invalidDriverId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .pathParam(ID_PARAMETER_NAME, invalidDriverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public DriverResponse getDriverByIdWhenDriverExistsRequest(int port, Long existingDriverId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingDriverId)
                .when()
                .get(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);
    }

    public ExceptionResponse getDriverByIdWhenDriverNotExistsRequest(int port, Long invalidDriverId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidDriverId)
                .when()
                .get(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public DriverPageResponse getAllDriversRequest(int port, int page, int size, String sortBy) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(DRIVER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverPageResponse.class);
    }

    public ExceptionResponse getAllDriversWhenIncorrectFieldRequest(int port, int page, int size, String sortBy) {
        return given()
                .port(port)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(DRIVER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public void deleteDriverWhenDriverExistsRequest(int port, Long existingDriverId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingDriverId)
                .when()
                .delete(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public void deleteDriverWhenDriverNotExistsRequest(int port, Long invalidDriverId) {
        given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidDriverId)
                .when()
                .delete(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }


    public DriverResponse changeStatusToFreeWhenChangeStatusBusyRequest(int port, Long driverId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam(ID_PARAMETER_NAME, driverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}/free")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);
    }

    public ExceptionResponse changeStatusToFreeWhenDriverNotFoundRequest(int port, Long invalidDriverId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam(ID_PARAMETER_NAME, invalidDriverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}/free")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public ExceptionResponse changeStatusToFreeWhenStatusAlreadyFreeRequest(int port, Long driverId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam(ID_PARAMETER_NAME, driverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}/free")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }
}