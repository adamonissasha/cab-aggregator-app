package com.example.driverservice.integration;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarPageResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.dto.response.ExceptionResponse;
import com.example.driverservice.dto.response.ValidationErrorResponse;
import com.example.driverservice.repository.CarRepository;
import com.example.driverservice.util.TestCarUtil;
import com.example.driverservice.util.client.CarClientUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:sql/add-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/delete-test-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Testcontainers
@ActiveProfiles("test")
public class CarControllerTest {
    @LocalServerPort
    int port;

    private final CarRepository carRepository;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    public CarControllerTest(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Test
    void createCar_WhenCarNumberUniqueAndDataValid_ShouldReturnCarResponse() {
        CarRequest carRequest = TestCarUtil.getCarRequest();
        CarResponse expected = TestCarUtil.getNewCarResponse();

        CarResponse actual = CarClientUtil.createCarWithUniqueCarNumberAndValidDataRequest(port, carRequest);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);

        deleteCarAfterTest(actual.getId());
    }

    @Test
    void createCar_WhenCarNumberAlreadyExists_ShouldReturnConflictResponse() {
        CarRequest carRequest = TestCarUtil.getCarRequestWithExistingNumber();
        ExceptionResponse expected = TestCarUtil.getCarNumberExistsExceptionResponse();

        ExceptionResponse actual = CarClientUtil.createCarWithExistingCarNumberRequest(port, carRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createCar_WhenDataNotValid_ShouldReturnBadRequestResponse() {
        CarRequest carRequest = TestCarUtil.getCarRequestWithInvalidData();
        ValidationErrorResponse expected = TestCarUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = CarClientUtil.createCarWithInvalidDataRequest(port, carRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editCar_WhenDataValid_ShouldReturnCarResponse() {
        Long carId = TestCarUtil.getSecondCarId();
        CarRequest carRequest = TestCarUtil.getCarRequest();
        CarResponse expected = TestCarUtil.getNewCarResponse();
        expected.setId(carId);

        CarResponse actual = CarClientUtil.editCarWithValidDataRequest(port, carRequest, carId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editCar_WhenInvalidData_ShouldReturnBadRequestResponse() {
        Long carId = TestCarUtil.getFirstCarId();
        CarRequest carRequest = TestCarUtil.getCarRequestWithInvalidData();
        ValidationErrorResponse expected = TestCarUtil.getValidationErrorResponse();

        ValidationErrorResponse actual = CarClientUtil.editCarWithInvalidDataRequest(port, carRequest, carId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editCar_WhenCarNotFound_ShouldReturnNotFoundResponse() {
        Long invalidCarId = TestCarUtil.getInvalidId();
        CarRequest carRequest = TestCarUtil.getCarRequest();
        ExceptionResponse expected = TestCarUtil.getCarNotFoundExceptionResponse();

        ExceptionResponse actual = CarClientUtil.editCarWhenCarNotFoundRequest(port, carRequest, invalidCarId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getCarById_WhenCarExists_ShouldReturnCarResponse() {
        Long existingCarId = TestCarUtil.getSecondCarId();
        CarResponse expected = TestCarUtil.getSecondCarResponse();

        CarResponse actual = CarClientUtil.getCarByIdRequest(port, existingCarId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getCarById_WhenCarNotExists_ShouldReturnNotFoundResponse() {
        Long invalidCarId = TestCarUtil.getInvalidId();
        ExceptionResponse expected = TestCarUtil.getCarNotFoundExceptionResponse();

        ExceptionResponse actual = CarClientUtil.getCarByIdWhenCarNotExistsRequest(port, invalidCarId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllCars_ShouldReturnCarPageResponse() {
        int page = TestCarUtil.getPageNumber();
        int size = TestCarUtil.getPageSize();
        String sortBy = TestCarUtil.getCorrectSortField();
        CarPageResponse expected = TestCarUtil.getCarPageResponse();

        CarPageResponse actual = CarClientUtil.getAllCarsRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllCars_WhenIncorrectField_ShouldReturnExceptionResponse() {
        int page = TestCarUtil.getPageNumber();
        int size = TestCarUtil.getPageSize();
        String sortBy = TestCarUtil.getIncorrectSortField();
        ExceptionResponse expected = TestCarUtil.getIncorrectFieldExceptionResponse();

        ExceptionResponse actual = CarClientUtil.getAllCarsWhenIncorrectFieldRequest(port, page, size, sortBy);

        assertThat(actual).isEqualTo(expected);
    }

    void deleteCarAfterTest(Long id) {
        carRepository.deleteById(id);
    }
}