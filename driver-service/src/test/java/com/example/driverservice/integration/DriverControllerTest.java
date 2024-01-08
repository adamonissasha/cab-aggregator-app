package com.example.driverservice.integration;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.dto.response.ValidationErrorResponse;
import com.example.driverservice.model.enums.Status;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.util.TestDriverUtil;
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
public class DriverControllerTest {
    @LocalServerPort
    private int port;
    private final DriverRepository driverRepository;
    private static final String DRIVER_SERVICE_URL = "driver";

    @Autowired
    public DriverControllerTest(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Test
    void createDriver_WhenPhoneNumberUniqueAndDataValid_ShouldReturnDriverResponse() {
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();
        DriverResponse expected = TestDriverUtil.getDriverResponse();

        DriverResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .when()
                .post(DRIVER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(DriverResponse.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);

        deleteDriverAfterTest(actual.getId());
    }

    @Test
    void createDriver_WhenPhoneNumberAlreadyExists_ShouldReturnConflictResponse() {
        DriverRequest driverRequest = TestDriverUtil.getDriverRequestWithExistingNumber();
        ExceptionResponse expected = TestDriverUtil.getPhoneNumberExistsExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .when()
                .post(DRIVER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createDriver_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        DriverRequest driverRequest = TestDriverUtil.getDriverRequestWithInvalidData();
        ValidationErrorResponse expected = TestDriverUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .when()
                .post(DRIVER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editDriver_WhenDataValid_ShouldReturnDriverResponse() {
        Long driverId = TestDriverUtil.getSecondDriverId();
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();
        DriverResponse expected = TestDriverUtil.getDriverResponse();
        expected.setId(driverId);
        expected.setRating(TestDriverUtil.getDriverRating().getAverageRating());

        DriverResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .pathParam("id", driverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editDriver_WhenInvalidData_ShouldReturnBadRequestResponse() {
        Long driverId = TestDriverUtil.getFirstDriverId();
        DriverRequest driverRequest = TestDriverUtil.getDriverRequestWithInvalidData();
        ValidationErrorResponse expected = TestDriverUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .pathParam("id", driverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editDriver_WhenDriverNotFound_ShouldReturnNotFoundResponse() {
        Long invalidDriverId = TestDriverUtil.getInvalidId();
        DriverRequest driverRequest = TestDriverUtil.getDriverRequest();
        ExceptionResponse expected = TestDriverUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(driverRequest)
                .pathParam("id", invalidDriverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getDriverById_WhenDriverExists_ShouldReturnDriverResponse() {
        Long existingDriverId = TestDriverUtil.getSecondDriverId();
        DriverResponse expected = TestDriverUtil.getSecondDriverResponse();

        DriverResponse actual = given()
                .port(port)
                .pathParam("id", existingDriverId)
                .when()
                .get(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getDriverById_WhenDriverNotExists_ShouldReturnNotFoundResponse() {
        Long invalidDriverId = TestDriverUtil.getInvalidId();
        ExceptionResponse expected = TestDriverUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .pathParam("id", invalidDriverId)
                .when()
                .get(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllDrivers_ShouldReturnDriverPageResponse() {
        int page = TestDriverUtil.getPageNumber();
        int size = TestDriverUtil.getPageSize();
        String sortBy = TestDriverUtil.getSortField();
        DriverPageResponse expected = TestDriverUtil.getDriverPageResponse();

        DriverPageResponse actual = given()
                .port(port)
                .param("page", page)
                .param("size", size)
                .param("sortBy", sortBy)
                .when()
                .get(DRIVER_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverPageResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteDriver_WhenDriverExists_ShouldReturnNotFoundAfterDeletion() {
        Long existingDriverId = TestDriverUtil.getSecondDriverId();

        given()
                .port(port)
                .pathParam("id", existingDriverId)
                .when()
                .delete(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void deleteDriver_WhenDriverNotExists_ShouldReturnNotFound() {
        Long invalidDriverId = TestDriverUtil.getInvalidId();

        given()
                .port(port)
                .pathParam("id", invalidDriverId)
                .when()
                .delete(DRIVER_SERVICE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }


    @Test
    void changeStatusToFree_whenChangeStatusBusy_ShouldReturnFreeDriverResponse() {
        Long driverId = TestDriverUtil.getThirdDriverId();
        DriverResponse expected = TestDriverUtil.getThirdDriverResponse();
        expected.setStatus(Status.FREE.name());

        DriverResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam("id", driverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}/free")
                .then()
                .statusCode(200)
                .extract()
                .as(DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void changeStatusToFree_whenDriverNotFound_ShouldReturnNotFoundResponse() {
        Long invalidDriverId = TestDriverUtil.getInvalidId();
        ExceptionResponse expected = TestDriverUtil.getDriverNotFoundExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam("id", invalidDriverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}/free")
                .then()
                .statusCode(404)
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void changeStatusToFree_whenStatusAlreadyFree_ShouldReturnConflictResponse() {
        Long driverId = TestDriverUtil.getSecondDriverId();
        ExceptionResponse expected = TestDriverUtil.getStatusExceptionResponse();

        ExceptionResponse actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam("id", driverId)
                .when()
                .put(DRIVER_SERVICE_URL + "/{id}/free")
                .then()
                .statusCode(409)
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    void deleteDriverAfterTest(Long id) {
        driverRepository.deleteById(id);
    }
}