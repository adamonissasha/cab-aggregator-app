package com.example.driverservice.component.stepDefinitions;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.response.CarPageResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.exception.CarNotFoundException;
import com.example.driverservice.exception.CarNumberUniqueException;
import com.example.driverservice.exception.IncorrectFieldNameException;
import com.example.driverservice.model.Car;
import com.example.driverservice.repository.CarRepository;
import com.example.driverservice.service.impl.CarServiceImpl;
import com.example.driverservice.util.FieldValidator;
import com.example.driverservice.util.TestCarUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CarServiceStepDefinitions {
    @Mock
    private CarRepository carRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    FieldValidator fieldValidator;

    @InjectMocks
    private CarServiceImpl carService;

    private CarRequest carRequest;
    private Car car;
    private CarResponse expected;
    private CarResponse actual;
    private Exception exception;
    private CarPageResponse actualPageResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("New car has unique car number {string}")
    public void newCarHasUniqueCarNumber(String carNumber) {
        car = TestCarUtil.getFirstCar();
        car.setNumber(carNumber);
        carRequest = TestCarUtil.getCarRequest();
        carRequest.setNumber(carNumber);
        expected = TestCarUtil.getNewCarResponse();

        when(carRepository.findCarByNumber(carRequest.getNumber()))
                .thenReturn(Optional.empty());
        when(modelMapper.map(carRequest, Car.class))
                .thenReturn(car);
        when(carRepository.save(any(Car.class)))
                .thenReturn(car);
        when(modelMapper.map(carNumber, CarResponse.class))
                .thenReturn(expected);

        Optional<Car> optionalCar = carRepository.findCarByNumber(carNumber);
        assertFalse(optionalCar.isPresent());
    }

    @Given("New car has existing car number {string}")
    public void newCarHasExistingCarNumber(String carNumber) {
        car = TestCarUtil.getFirstCar();
        car.setNumber(carNumber);
        carRequest = TestCarUtil.getCarRequest();
        carRequest.setNumber(carNumber);

        when(carRepository.findCarByNumber(carNumber))
                .thenReturn(Optional.of(car));

        Optional<Car> optionalCar = carRepository.findCarByNumber(carNumber);
        assertTrue(optionalCar.isPresent());
    }

    @When("Method createCar called")
    public void methodCreateCarCalled() {
        try {
            actual = carService.createCar(carRequest);
        } catch (CarNumberUniqueException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the created car")
    public void theResponseShouldContainTheDetailsOfTheCreatedCar() {
        car = carRepository.save(car);
        expected = carService.mapCarToCarResponse(car);

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The CarNumberUniqueException should be thrown with message {string}")
    public void theCarNumberUniqueExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("Editing car with id {long} exists and car number {string} unique")
    public void editingCarWithIdExistsAndCarNumberUnique(long id, String carNumber) {
        carRequest = TestCarUtil.getCarRequest();
        car = TestCarUtil.getFirstCar();
        expected = TestCarUtil.getNewCarResponse();

        when(carRepository.findById(id))
                .thenReturn(Optional.of(car));
        when(carRepository.findCarByNumber(carNumber))
                .thenReturn(Optional.empty());
        when(modelMapper.map(carRequest, Car.class))
                .thenReturn(car);
        when(carRepository.save(car))
                .thenReturn(car);
        when(modelMapper.map(car, CarResponse.class))
                .thenReturn(expected);

        Optional<Car> optionalPassenger = carRepository.findCarByNumber(carNumber);
        assertFalse(optionalPassenger.isPresent());
    }

    @Given("Editing car with id {long} has existing car number {string}")
    public void editingCarWithIdHasExistingCarNumber(long id, String carNumber) {
        car = TestCarUtil.getFirstCar();
        car.setNumber(carNumber);
        carRequest = TestCarUtil.getCarRequest();
        carRequest.setNumber(carNumber);

        when(carRepository.findById(id))
                .thenReturn(Optional.of(car));
        when(carRepository.findCarByNumber(carNumber))
                .thenReturn(Optional.of(car));

        Optional<Car> optionalCar = carRepository.findCarByNumber(carNumber);
        assertTrue(optionalCar.isPresent());
    }

    @Given("There is no car with id {long}")
    public void thereIsNoCarWithId(long id) {
        car = TestCarUtil.getFirstCar();
        car.setId(id);

        when(carRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Car> car = carRepository.findById(id);
        assertFalse(car.isPresent());
    }

    @When("Method editCar called with id {long}")
    public void methodEditCarCalledWithId(long id) {
        try {
            actual = carService.editCar(id, carRequest);
        } catch (CarNumberUniqueException | CarNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the edited car")
    public void theResponseShouldContainTheDetailsOfTheEditedCar() {
        car = carRepository.save(car);
        expected = carService.mapCarToCarResponse(car);

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The CarNotFoundException should be thrown with message {string}")
    public void theCarNotFoundExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("There is a car with id {long}")
    public void thereIsACarWithId(long id) {
        car = TestCarUtil.getFirstCar();
        expected = TestCarUtil.getFirstCarResponse();

        when(carRepository.findById(id))
                .thenReturn(Optional.of(car));
        when(modelMapper.map(car, CarResponse.class))
                .thenReturn(expected);

        assertTrue(carRepository.findById(id).isPresent());
    }

    @When("Method getCarById called with id {long}")
    public void methodGetCarByIdCalledWithId(long id) {
        try {
            actual = carService.getCarById(id);
        } catch (CarNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of car with id {long}")
    public void theResponseShouldContainTheDetailsOfCarWithId(long id) {
        car = carRepository.findById(id).get();
        expected = carService.mapCarToCarResponse(car);

        assertThat(actual).isEqualTo(expected);
    }

    @Given("There are cars in the system in page {int} with size {int} and sort by {string}")
    public void thereAreCarsInTheSystemInPageWithSizeAndSortBy(int page, int size, String sortBy) {
        List<Car> cars = TestCarUtil.getCars();
        List<CarResponse> carResponses = TestCarUtil.getCarResponses();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Car> mockCarPage = new PageImpl<>(cars, pageable, cars.size());

        when(carRepository.findAll(any(Pageable.class)))
                .thenReturn(mockCarPage);
        when(modelMapper.map(cars.get(0), CarResponse.class))
                .thenReturn(carResponses.get(0));
        when(modelMapper.map(cars.get(1), CarResponse.class))
                .thenReturn(carResponses.get(1));

        Page<Car> carPage = carRepository.findAll(pageable);
        assertTrue(carPage.hasContent());
    }

    @When("Method getAllCars called with page {int}, size {int}, and sort by {string}")
    public void methodGetAllCarsCalledWithPageSizeAndSortBy(int page, int size, String sortBy) {
        try {
            actualPageResponse = carService.getAllCars(page, size, sortBy);
        } catch (IncorrectFieldNameException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain a page of cars number {int} with size {int}")
    public void theResponseShouldContainAPageOfCarsNumberWithSize(int page, int size) {
        List<CarResponse> carResponses = TestCarUtil.getCarResponses();

        CarPageResponse expectedPageResponse = CarPageResponse.builder()
                .cars(carResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        assertEquals(expectedPageResponse, actualPageResponse);
    }
}
