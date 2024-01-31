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
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@UtilityClass
public class RideClientUtil {
    private final String RIDE_SERVICE_URL = "ride";
    private final String ID_PARAMETER_NAME = "id";
    private final String PAGE_PARAMETER_NAME = "page";
    private final String SIZE_PARAMETER_NAME = "size";
    private final String SORT_PARAMETER_NAME = "sortBy";
    private final String PASSENGER_ID_PARAMETER_NAME = "passengerId";
    private final String DRIVER_ID_PARAMETER_NAME = "driverId";

    public PassengerRideResponse createRideWhenDataValidRequest(int port, CreateRideRequest createRideRequest) {
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

    public ExceptionResponse createRideWhenPassengerRideAlreadyExistsRequest(int port,
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

    public ExceptionResponse createRideWhenIncorrectPaymentMethodRequest(int port,
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

    public ValidationErrorResponse createRideWhenDataInvalidRequest(int port, CreateRideRequest createRideRequest) {
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

    public RideResponse getRideByRideIdWhenRideExistsRequest(int port, Long existingRideId) {
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

    public ExceptionResponse getRideByRideIdWhenRideNotExistsRequest(int port, Long invalidRideId) {
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

    public PassengerRideResponse editRideWhenValidDataRequest(int port, Long rideId, EditRideRequest editRideRequest) {
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

    public ValidationErrorResponse editRideWhenInvalidDataRequest(int port,
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

    public ExceptionResponse editRideWhenRideNotFoundRequest(int port, EditRideRequest editRideRequest, Long invalidRideId) {
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

    public ExceptionResponse editRideWhenStatusCanceledRequest(int port,
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

    public RideResponse startRideRequest(int port, Long rideId) {
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

    public ExceptionResponse startRideWhenRideNotFoundRequest(int port, Long invalidRideId) {
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

    public ExceptionResponse startRideWhenStatusCanceledRequest(int port, Long rideId) {
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

    public RideResponse cancelRideRequest(int port, Long rideId) {
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

    public ExceptionResponse cancelRideWhenRideNotFoundRequest(int port, Long invalidRideId) {
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

    public ExceptionResponse cancelRideWhenStatusCanceledRequest(int port, Long rideId) {
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

    public RideResponse completeRideRequest(int port, Long rideId) {
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

    public ExceptionResponse completeRideWhenRideNotFoundRequest(int port, Long invalidRideId) {
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

    public ExceptionResponse completeRideWhenStatusCanceledRequest(int port, Long rideId) {
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

    public PassengerRidesPageResponse getAllPassengerRidesRequest(int port, int page, int size,
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

    public ExceptionResponse getAllPassengerRidesWhenIncorrectFieldRequest(int port, int page, int size,
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

    public RidesPageResponse getAllDriverRidesRequest(int port, int page, int size,
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

    public ExceptionResponse getAllDriverRidesWhenIncorrectFieldRequest(int port, int page, int size,
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

    public void ratePassengerWhenRideExistsRequest(int port, Long rideId, RatingRequest ratingRequest) {
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

    public ExceptionResponse ratePassengerWhenRideNotFoundRequest(int port,
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

    public void rateDriverWhenRideExistsRequest(int port, Long rideId, RatingRequest ratingRequest) {
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

    public ExceptionResponse rateDriverWhenRideNotFoundRequest(int port,
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
