package com.example.driverservice.component.stepDefinitions;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.dto.response.DriverPageResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.exception.CarNotFoundException;
import com.example.driverservice.exception.DriverNotFoundException;
import com.example.driverservice.exception.DriverStatusException;
import com.example.driverservice.exception.IncorrectFieldNameException;
import com.example.driverservice.exception.PhoneNumberUniqueException;
import com.example.driverservice.kafka.service.KafkaFreeDriverService;
import com.example.driverservice.model.Driver;
import com.example.driverservice.model.enums.Status;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.CarService;
import com.example.driverservice.service.DriverRatingService;
import com.example.driverservice.service.impl.DriverServiceImpl;
import com.example.driverservice.util.FieldValidator;
import com.example.driverservice.util.TestCarUtil;
import com.example.driverservice.util.TestDriverUtil;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class DriverServiceStepDefinitions {
    @Mock
    private DriverRepository driverRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    CarService carService;

    @Mock
    private DriverRatingService driverRatingService;

    @Mock
    FieldValidator fieldValidator;

    @Mock
    KafkaFreeDriverService kafkaFreeDriverService;

    @InjectMocks
    private DriverServiceImpl driverService;

    private DriverRequest driverRequest;
    private Driver driver;
    private DriverResponse expected;
    private DriverResponse actual;
    private Exception exception;
    private CarResponse carResponse;
    private AverageDriverRatingResponse driverRating;
    private DriverPageResponse actualPageResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("New driver has unique phone number {string}")
    public void newDriverHasUniquePhoneNumber(String phoneNumber) {
        driver = TestDriverUtil.getFirstDriver();
        driver.setPhoneNumber(phoneNumber);
        driverRequest = TestDriverUtil.getDriverRequest();
        driverRequest.setPhoneNumber(phoneNumber);
        driverRating = TestDriverUtil.getFirstDriverRating();
        expected = TestDriverUtil.getDriverResponse();
        carResponse = TestCarUtil.getFirstCarResponse();

        when(carService.getCarById(driverRequest.getCarId()))
                .thenReturn(carResponse);
        when(driverRepository.findDriverByPhoneNumber(driverRequest.getPhoneNumber())).
                thenReturn(Optional.empty());
        when(modelMapper.map(any(DriverRequest.class), eq(Driver.class)))
                .thenReturn(driver);
        when(driverRepository.save(any(Driver.class)))
                .thenReturn(TestDriverUtil.getFirstDriver());
        when(driverRatingService.getAverageDriverRating(anyLong()))
                .thenReturn(driverRating);
        when(modelMapper.map(any(Driver.class), eq(DriverResponse.class)))
                .thenReturn(expected);
        doNothing()
                .when(kafkaFreeDriverService)
                .sendFreeDriverToConsumer(any(DriverResponse.class));

        Optional<Driver> optionalDriver = driverRepository.findDriverByPhoneNumber(phoneNumber);
        assertFalse(optionalDriver.isPresent());
    }

    @Given("New driver has existing phone number {string}")
    public void newDriverHasExistingPhoneNumber(String phoneNumber) {
        driver = TestDriverUtil.getFirstDriver();
        driver.setPhoneNumber(phoneNumber);
        driverRequest = TestDriverUtil.getDriverRequest();
        driverRequest.setPhoneNumber(phoneNumber);

        when(driverRepository.findDriverByPhoneNumber(phoneNumber))
                .thenReturn(Optional.of(driver));

        Optional<Driver> optionalDriver = driverRepository.findDriverByPhoneNumber(phoneNumber);
        assertTrue(optionalDriver.isPresent());
    }

    @Given("New driver has non-existing car with id {long}")
    public void newDriverHasNonExistingCarWithId(long id) {
        driverRequest = TestDriverUtil.getDriverRequest();
        driverRequest.setCarId(id);

        when(carService.getCarById(id))
                .thenThrow(new CarNotFoundException(String.format(TestDriverUtil.getCarNotFoundMessage(), id)));
    }

    @When("Method createDriver called")
    public void methodCreateDriverCalled() {
        try {
            actual = driverService.createDriver(driverRequest);
        } catch (PhoneNumberUniqueException | CarNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the created driver")
    public void theResponseShouldContainTheDetailsOfTheCreatedDriver() {
        driver = driverRepository.save(driver);
        expected = driverService.mapDriverToDriverResponse(driver);

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The PhoneNumberUniqueException should be thrown with message {string}")
    public void thePhoneNumberUniqueExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Then("The CarNotFoundException should be thrown in method createDriver with message {string}")
    public void theCarNotFoundExceptionShouldBeThrownInMethodCreateDriverWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("Editing driver with id {long} exists and phone number {string} unique")
    public void editingDriverWithIdExistsAndPhoneNumberUnique(long id, String phoneNumber) {
        driverRequest = TestDriverUtil.getDriverRequest();
        driver = TestDriverUtil.getFirstDriver();
        Driver existingDriver = TestDriverUtil.getSecondDriver();
        expected = TestDriverUtil.getDriverResponse();
        driverRating = TestDriverUtil.getFirstDriverRating();

        when(driverRepository.findById(id))
                .thenReturn(Optional.of(existingDriver));
        when(driverRepository.findDriverByPhoneNumber(driverRequest.getPhoneNumber()))
                .thenReturn(Optional.empty());
        when(modelMapper.map(driverRequest, Driver.class))
                .thenReturn(driver);
        when(driverRepository.save(driver))
                .thenReturn(driver);
        when(driverRatingService.getAverageDriverRating(anyLong()))
                .thenReturn(driverRating);
        when(modelMapper.map(driver, DriverResponse.class))
                .thenReturn(expected);

        Optional<Driver> optionalDriver = driverRepository.findDriverByPhoneNumber(phoneNumber);
        assertFalse(optionalDriver.isPresent());
    }

    @Given("Editing driver with id {long} has existing phone number {string}")
    public void editingDriverWithIdHasExistingPhoneNumber(long id, String phoneNumber) {
        driver = TestDriverUtil.getFirstDriver();
        driver.setPhoneNumber(phoneNumber);
        driverRequest = TestDriverUtil.getDriverRequest();
        driverRequest.setPhoneNumber(phoneNumber);

        when(driverRepository.findById(id))
                .thenReturn(Optional.of(driver));
        when(driverRepository.findDriverByPhoneNumber(phoneNumber))
                .thenReturn(Optional.of(driver));

        Optional<Driver> optionalDriver = driverRepository.findDriverByPhoneNumber(phoneNumber);
        assertTrue(optionalDriver.isPresent());
    }

    @Given("There is no driver with id {long}")
    public void thereIsNoDriverWithId(long id) {
        driverRequest = TestDriverUtil.getDriverRequest();

        when(driverRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Driver> driver = driverRepository.findById(id);
        assertFalse(driver.isPresent());
    }

    @When("Method editDriver called with id {long}")
    public void methodEditDriverCalledWithId(long id) {
        try {
            actual = driverService.editDriver(id, driverRequest);
        } catch (PhoneNumberUniqueException | DriverNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the edited driver")
    public void theResponseShouldContainTheDetailsOfTheEditedDriver() {
        driver = driverRepository.save(driver);
        expected = driverService.mapDriverToDriverResponse(driver);

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The DriverNotFoundException should be thrown with message {string}")
    public void theDriverNotFoundExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("There is a driver with id {long}")
    public void thereIsADriverWithId(long id) {
        driver = TestDriverUtil.getFirstDriver();
        expected = TestDriverUtil.getDriverResponse();
        driverRating = TestDriverUtil.getFirstDriverRating();

        when(driverRepository.findById(id))
                .thenReturn(Optional.of(driver));
        when(driverRatingService.getAverageDriverRating(anyLong()))
                .thenReturn(driverRating);
        when(modelMapper.map(driver, DriverResponse.class))
                .thenReturn(expected);

        assertTrue(driverRepository.findById(id).isPresent());
    }

    @When("Method getDriverById called with id {long}")
    public void methodGetDriverByIdCalledWithId(long id) {
        try {
            actual = driverService.getDriverById(id);
        } catch (DriverNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of driver with id {long}")
    public void theResponseShouldContainTheDetailsOfDriverWithId(long id) {
        driver = driverRepository.findById(id).get();
        expected = driverService.mapDriverToDriverResponse(driver);

        assertThat(actual).isEqualTo(expected);
    }

    @Given("There are drivers in the system in page {int} with size {int} and sort by {string}")
    public void thereAreDriversInTheSystemInPageWithSizeAndSortBy(int page, int size, String sortBy) {
        List<Driver> drivers = TestDriverUtil.getDrivers();
        List<DriverResponse> driverResponses = TestDriverUtil.getDriverResponses();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Driver> mockDriverPage = new PageImpl<>(drivers, pageable, drivers.size());
        carResponse = TestCarUtil.getFirstCarResponse();
        when(carService.getCarById(anyLong()))
                .thenReturn(carResponse);
        when(driverRepository.findAll(any(Pageable.class)))
                .thenReturn(mockDriverPage);
        when(modelMapper.map(drivers.get(0), DriverResponse.class))
                .thenReturn(driverResponses.get(0));
        when(modelMapper.map(drivers.get(1), DriverResponse.class))
                .thenReturn(driverResponses.get(1));

        Page<Driver> driverPage = driverRepository.findAll(pageable);
        assertTrue(driverPage.hasContent());
    }

    @When("Method getAllDrivers called with page {int}, size {int}, and sort by {string}")
    public void methodGetAllDriversCalledWithPageSizeAndSortBy(int page, int size, String sortBy) {
        when(driverRatingService.getAverageDriverRating(TestDriverUtil.getFirstDriverId()))
                .thenReturn(TestDriverUtil.getFirstDriverRating());
        when(driverRatingService.getAverageDriverRating(TestDriverUtil.getSecondDriverId()))
                .thenReturn(TestDriverUtil.getSecondDriverRating());

        try {
            actualPageResponse = driverService.getAllDrivers(page, size, sortBy);
        } catch (IncorrectFieldNameException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain a page of drivers number {int} with size {int}")
    public void theResponseShouldContainAPageOfDriversNumberWithSize(int page, int size) {
        List<DriverResponse> driverResponses = TestDriverUtil.getDriverResponses();

        DriverPageResponse expectedPageResponse = DriverPageResponse.builder()
                .drivers(driverResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        assertEquals(expectedPageResponse, actualPageResponse);
    }

    @Given("There is driver with id {long} and status BUSY")
    public void thereIsDriverWithIdAndStatusBUSY(long id) {
        driver = TestDriverUtil.getFirstDriver();
        driver.setStatus(Status.BUSY);
        driverRating = TestDriverUtil.getFirstDriverRating();
        expected = TestDriverUtil.getDriverResponse();

        when(driverRepository.findById(id))
                .thenReturn(Optional.of(driver));
        when(driverRepository.save(driver))
                .thenReturn(driver);
        when(driverRatingService.getAverageDriverRating(anyLong()))
                .thenReturn(driverRating);
        when(modelMapper.map(driver, DriverResponse.class))
                .thenReturn(expected);
    }

    @Given("Status of driver with id {long} already free")
    public void statusOfDriverWithIdAlreadyFree(long id) {
        driver = TestDriverUtil.getFirstDriver();

        when(driverRepository.findById(id))
                .thenReturn(Optional.of(driver));

        assertTrue(driverRepository.findById(id).isPresent());
        assertEquals(Status.FREE, driver.getStatus());
    }

    @When("Method changeDriverStatusToFree called with id {long}")
    public void methodChangeDriverStatusToFreeCalledWithId(long id) {
        try {
            actual = driverService.changeDriverStatusToFree(id);
        } catch (DriverNotFoundException | DriverStatusException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the changing driver with id {long}")
    public void theResponseShouldContainTheDetailsOfTheChangingDriver(long id) {
        assertEquals(Status.FREE, driver.getStatus());
    }

    @Then("The DriverStatusException should be thrown with message {string};")
    public void theDriverStatusExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }
}
