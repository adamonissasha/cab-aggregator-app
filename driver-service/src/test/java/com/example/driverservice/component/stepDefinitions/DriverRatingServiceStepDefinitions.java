package com.example.driverservice.component.stepDefinitions;


import com.example.driverservice.dto.request.DriverRatingRequest;
import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.DriverRatingResponse;
import com.example.driverservice.exception.DriverNotFoundException;
import com.example.driverservice.model.Driver;
import com.example.driverservice.model.DriverRating;
import com.example.driverservice.repository.DriverRatingRepository;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.impl.DriverRatingServiceImpl;
import com.example.driverservice.util.TestDriverRatingUtil;
import com.example.driverservice.util.TestDriverUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class DriverRatingServiceStepDefinitions {
    @Mock
    private DriverRatingRepository driverRatingRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DriverRatingServiceImpl driverRatingService;

    private Driver driver;
    private Exception exception;
    private AverageDriverRatingResponse actualAverageDriverRatingResponse;
    private AllDriverRatingsResponse actualAllDriverRatingsResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("Rating driver with id {long} exists")
    public void ratingDriverWithIdExists(long id) {
        driver = TestDriverUtil.getFirstDriver();

        when(driverRepository.findById(id))
                .thenReturn(Optional.of(driver));

        Optional<Driver> optionalDriver = driverRepository.findById(id);
        assertTrue(optionalDriver.isPresent());
    }

    @Given("Rating driver with id {long} not exists")
    public void ratingDriverWithIdNotExists(long id) {
        driver = TestDriverUtil.getFirstDriver();
        driver.setId(id);

        when(driverRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Driver> driver = driverRepository.findById(id);
        assertFalse(driver.isPresent());
    }

    @When("Method rateDriver called with id {long}")
    public void methodRateDriverCalledWithId(long id) {
        DriverRatingRequest driverRatingRequest = TestDriverRatingUtil.getDriverRatingRequest();
        driverRatingRequest.setDriverId(id);

        try {
            driverRatingService.rateDriver(driverRatingRequest);
        } catch (DriverNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The DriverNotFoundException should be thrown with the following message {string}")
    public void theDriverNotFoundExceptionShouldBeThrownWithTheFollowingMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("Driver with id {long} to retrieval all ratings exists")
    public void driverWithIdToRetrievalAllRatingsExists(long id) {
        Driver driver = TestDriverUtil.getFirstDriver();

        when(driverRepository.findById(id))
                .thenReturn(Optional.of(driver));

        Optional<Driver> optionalDriver = driverRepository.findById(id);
        assertTrue(optionalDriver.isPresent());
    }

    @When("Method getRatingsByDriverId called with id {int}")
    public void methodGetRatingsByDriverIdCalledWithId(long id) {
        DriverRating firstDriverRating = TestDriverRatingUtil.getFirstDriverRating();
        DriverRating secondDriverRating = TestDriverRatingUtil.getSecondDriverRating();
        DriverRatingResponse firstDriverRatingResponse = TestDriverRatingUtil.getFirstDriverRatingResponse();
        DriverRatingResponse secondDriverRatingResponse = TestDriverRatingUtil.getSecondDriverRatingResponse();
        List<DriverRating> driverRatings = Arrays.asList(firstDriverRating, secondDriverRating);

        when(modelMapper.map(firstDriverRating, DriverRatingResponse.class))
                .thenReturn(firstDriverRatingResponse);
        when(modelMapper.map(secondDriverRating, DriverRatingResponse.class))
                .thenReturn(secondDriverRatingResponse);
        when(driverRatingRepository.getDriverRatingsByDriverId(id))
                .thenReturn(driverRatings);

        try {
            actualAllDriverRatingsResponse = driverRatingService.getRatingsByDriverId(id);
        } catch (DriverNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contains the list of driver ratings")
    public void theResponseShouldContainsTheListOfDriverRatings() {
        DriverRatingResponse firstDriverRatingResponse = TestDriverRatingUtil.getFirstDriverRatingResponse();
        DriverRatingResponse secondDriverRatingResponse = TestDriverRatingUtil.getSecondDriverRatingResponse();
        List<DriverRatingResponse> expectedDriverRatings = Arrays.asList(firstDriverRatingResponse, secondDriverRatingResponse);
        AllDriverRatingsResponse expected = AllDriverRatingsResponse.builder()
                .driverRatings(expectedDriverRatings)
                .build();

        assertEquals(expected, actualAllDriverRatingsResponse);
    }

    @Given("Driver with id {long} to retrieval average rating exists")
    public void driverWithIdToRetrievalAverageRatingExists(long id) {
        Driver driver = TestDriverUtil.getFirstDriver();

        when(driverRepository.findById(id))
                .thenReturn(Optional.of(driver));

        Optional<Driver> optionalDriver = driverRepository.findById(id);
        assertTrue(optionalDriver.isPresent());
    }

    @When("Method getAverageDriverRating called with id {long}")
    public void methodGetAverageDriverRatingCalledWithId(long id) {
        DriverRating firstDriverRating = TestDriverRatingUtil.getFirstDriverRating();
        DriverRating secondDriverRating = TestDriverRatingUtil.getSecondDriverRating();
        List<DriverRating> driverRatings = Arrays.asList(firstDriverRating, secondDriverRating);

        when(driverRatingRepository.getDriverRatingsByDriverId(id))
                .thenReturn(driverRatings);

        try {
            actualAverageDriverRatingResponse = driverRatingService.getAverageDriverRating(id);
        } catch (DriverNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contains the average driver with id {long} rating")
    public void theResponseShouldContainsTheAverageDriverWithIdRating(long id) {
        Double expectedAverageRating = TestDriverRatingUtil.getAverageDriverRating();
        AverageDriverRatingResponse expected =
                AverageDriverRatingResponse.builder()
                        .averageRating(expectedAverageRating)
                        .driverId(id)
                        .build();

        assertEquals(expected, actualAverageDriverRatingResponse);
    }
}
