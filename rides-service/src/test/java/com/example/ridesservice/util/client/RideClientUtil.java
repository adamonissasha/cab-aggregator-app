package com.example.ridesservice.util.client;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.request.RatingRequest;
import com.example.ridesservice.dto.response.ExceptionResponse;
import com.example.ridesservice.dto.response.PassengerRideResponse;
import com.example.ridesservice.dto.response.PassengerRidesPageResponse;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.dto.response.RidesPageResponse;
import com.example.ridesservice.dto.response.ValidationErrorResponse;
import io.restassured.http.ContentType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RideClientUtil {
    static String RIDE_SERVICE_URL = "ride";
    static String ID_PARAMETER_NAME = "id";
    static String PAGE_PARAMETER_NAME = "page";
    static String SIZE_PARAMETER_NAME = "size";
    static String SORT_PARAMETER_NAME = "sortBy";
    static String PASSENGER_ID_PARAMETER_NAME = "passengerId";
    static String DRIVER_ID_PARAMETER_NAME = "driverId";

    public static PassengerRideResponse createRideWhenDataValidRequest(int port, CreateRideRequest createRideRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createRideRequest)
                .when()
                .post(RIDE_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(PassengerRideResponse.class);
    }

    public static ExceptionResponse createRideWhenPassengerRideAlreadyExistsRequest(int port,
                                                                                    CreateRideRequest createRideRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createRideRequest)
                .when()
                .post(RIDE_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static ExceptionResponse createRideWhenIncorrectPaymentMethodRequest(int port,
                                                                                CreateRideRequest createRideRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createRideRequest)
                .when()
                .post(RIDE_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static ValidationErrorResponse createRideWhenDataInvalidRequest(int port, CreateRideRequest createRideRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createRideRequest)
                .when()
                .post(RIDE_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public static RideResponse getRideByRideIdWhenRideExistsRequest(int port, Long existingRideId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, existingRideId)
                .when()
                .get(RIDE_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);
    }

    public static ExceptionResponse getRideByRideIdWhenRideNotExistsRequest(int port, Long invalidRideId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidRideId)
                .when()
                .get(RIDE_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static PassengerRideResponse editRideWhenValidDataRequest(int port, Long rideId, EditRideRequest editRideRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(editRideRequest)
                .pathParam(ID_PARAMETER_NAME, rideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerRideResponse.class);
    }

    public static ValidationErrorResponse editRideWhenInvalidDataRequest(int port,
                                                                         EditRideRequest editRideRequest,
                                                                         Long rideId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(editRideRequest)
                .pathParam(ID_PARAMETER_NAME, rideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);
    }

    public static ExceptionResponse editRideWhenRideNotFoundRequest(int port, EditRideRequest editRideRequest, Long invalidRideId) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(editRideRequest)
                .pathParam(ID_PARAMETER_NAME, invalidRideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static ExceptionResponse editRideWhenStatusCanceledRequest(int port,
                                                                      Long rideId,
                                                                      EditRideRequest editRideRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(editRideRequest)
                .pathParam(ID_PARAMETER_NAME, rideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static RideResponse startRideRequest(int port, Long rideId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, rideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}/start")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);
    }

    public static ExceptionResponse startRideWhenRideNotFoundRequest(int port, Long invalidRideId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidRideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}/start")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static ExceptionResponse startRideWhenStatusCanceledRequest(int port, Long rideId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, rideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}/start")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static RideResponse cancelRideRequest(int port, Long rideId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, rideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}/cancel")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);
    }

    public static ExceptionResponse cancelRideWhenRideNotFoundRequest(int port, Long invalidRideId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidRideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}/cancel")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static ExceptionResponse cancelRideWhenStatusCanceledRequest(int port, Long rideId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, rideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}/cancel")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static RideResponse completeRideRequest(int port, Long rideId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, rideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}/complete")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);
    }

    public static ExceptionResponse completeRideWhenRideNotFoundRequest(int port, Long invalidRideId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, invalidRideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}/complete")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static ExceptionResponse completeRideWhenStatusCanceledRequest(int port, Long rideId) {
        return given()
                .port(port)
                .pathParam(ID_PARAMETER_NAME, rideId)
                .when()
                .put(RIDE_SERVICE_URL + "/{id}/complete")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static PassengerRidesPageResponse getAllPassengerRidesRequest(int port, int page, int size,
                                                                         String sortBy, Long passengerId) {
        return given()
                .port(port)
                .pathParam(PASSENGER_ID_PARAMETER_NAME, passengerId)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(RIDE_SERVICE_URL + "/passenger/{passengerId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerRidesPageResponse.class);
    }

    public static ExceptionResponse getAllPassengerRidesWhenIncorrectFieldRequest(int port, int page, int size,
                                                                                  String sortBy, Long passengerId) {
        return given()
                .port(port)
                .pathParam(PASSENGER_ID_PARAMETER_NAME, passengerId)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(RIDE_SERVICE_URL + "/passenger/{passengerId}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static RidesPageResponse getAllDriverRidesRequest(int port, int page, int size,
                                                             String sortBy, Long driverId) {
        return given()
                .port(port)
                .pathParam(DRIVER_ID_PARAMETER_NAME, driverId)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(RIDE_SERVICE_URL + "/driver/{driverId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RidesPageResponse.class);
    }

    public static ExceptionResponse getAllDriverRidesWhenIncorrectFieldRequest(int port, int page, int size,
                                                                               String sortBy, Long driverId) {
        return given()
                .port(port)
                .pathParam(DRIVER_ID_PARAMETER_NAME, driverId)
                .param(PAGE_PARAMETER_NAME, page)
                .param(SIZE_PARAMETER_NAME, size)
                .param(SORT_PARAMETER_NAME, sortBy)
                .when()
                .get(RIDE_SERVICE_URL + "/driver/{driverId}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static void ratePassengerWhenRideExistsRequest(int port, Long rideId, RatingRequest ratingRequest) {
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(ratingRequest)
                .pathParam(ID_PARAMETER_NAME, rideId)
                .when()
                .post(RIDE_SERVICE_URL + "/{id}/passenger/rate")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public static ExceptionResponse ratePassengerWhenRideNotFoundRequest(int port,
                                                                         Long invalidRideId,
                                                                         RatingRequest ratingRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(ratingRequest)
                .pathParam(ID_PARAMETER_NAME, invalidRideId)
                .when()
                .post(RIDE_SERVICE_URL + "/{id}/passenger/rate")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }

    public static void rateDriverWhenRideExistsRequest(int port, Long rideId, RatingRequest ratingRequest) {
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(ratingRequest)
                .pathParam(ID_PARAMETER_NAME, rideId)
                .when()
                .post(RIDE_SERVICE_URL + "/{id}/driver/rate")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public static ExceptionResponse rateDriverWhenRideNotFoundRequest(int port,
                                                                      Long invalidRideId,
                                                                      RatingRequest ratingRequest) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(ratingRequest)
                .pathParam(ID_PARAMETER_NAME, invalidRideId)
                .when()
                .post(RIDE_SERVICE_URL + "/{id}/driver/rate")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);
    }
}
