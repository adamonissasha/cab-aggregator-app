package com.example.driverservice.integration;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarPageResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.dto.response.ValidationErrorResponse;
import com.example.driverservice.repository.CarRepository;
import com.example.driverservice.util.TestCarUtil;
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
public class CarControllerTest {
    @LocalServerPort
    private int port;
    private final CarRepository carRepository;
    private static final String CAR_SERVICE_URL = "driver/car";

    @Autowired
    public CarControllerTest(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Test
    void createCar_WhenCarNumberUniqueAndDataValid_ShouldReturnCarResponse() {
        CarRequest carRequest = TestCarUtil.getCarRequest();
        CarResponse expected = TestCarUtil.getNewCarResponse();

        CarResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .when()
                .post(CAR_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(CarResponse.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);

        deleteCarAfterTest(actual.getId());
    }

    @Test
    void createCar_WhenCarNumberAlreadyExists_ShouldReturnConflictResponse() {
        CarRequest carRequest = TestCarUtil.getCarRequestWithExistingNumber();
        ExceptionResponse expected = TestCarUtil.getPhoneNumberExistsExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .when()
                .post(CAR_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createCar_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        CarRequest carRequest = TestCarUtil.getCarRequestWithInvalidData();
        ValidationErrorResponse expected = TestCarUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .when()
                .post(CAR_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editCar_WhenDataValid_ShouldReturnCarResponse() {
        Long carId = TestCarUtil.getSecondCarId();
        CarRequest carRequest = TestCarUtil.getCarRequest();
        CarResponse expected = TestCarUtil.getNewCarResponse();
        expected.setId(carId);

        CarResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .pathParam("id", carId)
                .when()
                .put(CAR_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CarResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editCar_WhenInvalidData_ShouldReturnBadRequestResponse() {
        Long carId = TestCarUtil.getFirstCarId();
        CarRequest carRequest = TestCarUtil.getCarRequestWithInvalidData();
        ValidationErrorResponse expected = TestCarUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .pathParam("id", carId)
                .when()
                .put(CAR_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editCar_WhenCarNotFound_ShouldReturnNotFoundResponse() {
        Long invalidCarId = TestCarUtil.getInvalidId();
        CarRequest carRequest = TestCarUtil.getCarRequest();
        ExceptionResponse expected = TestCarUtil.getCarNotFoundExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(carRequest)
                .pathParam("id", invalidCarId)
                .when()
                .put(CAR_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getCarById_WhenCarExists_ShouldReturnCarResponse() {
        Long existingCarId = TestCarUtil.getSecondCarId();
        CarResponse expected = TestCarUtil.getSecondCarResponse();

        CarResponse actual = given()
                .port(port)
                .pathParam("id", existingCarId)
                .when()
                .get(CAR_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CarResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getCarById_WhenCarNotExists_ShouldReturnNotFoundResponse() {
        Long invalidCarId = TestCarUtil.getInvalidId();
        ExceptionResponse expected = TestCarUtil.getCarNotFoundExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .pathParam("id", invalidCarId)
                .when()
                .get(CAR_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllCars_ShouldReturnCarPageResponse() {
        int page = TestCarUtil.getPageNumber();
        int size = TestCarUtil.getPageSize();
        String sortBy = TestCarUtil.getSortField();
        CarPageResponse expected = TestCarUtil.getCarPageResponse();

        CarPageResponse actual = given()
                .port(port)
                .param("page", page)
                .param("size", size)
                .param("sortBy", sortBy)
                .when()
                .get(CAR_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CarPageResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    void deleteCarAfterTest(Long id) {
        carRepository.deleteById(id);
    }
}