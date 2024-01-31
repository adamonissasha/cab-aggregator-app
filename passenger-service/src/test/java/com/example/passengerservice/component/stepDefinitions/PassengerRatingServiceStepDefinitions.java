package com.example.passengerservice.component.stepDefinitions;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;
import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.PassengerRating;
import com.example.passengerservice.repository.PassengerRatingRepository;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.impl.PassengerRatingServiceImpl;
import com.example.passengerservice.util.TestPassengerRatingUtil;
import com.example.passengerservice.util.TestPassengerUtil;
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

public class PassengerRatingServiceStepDefinitions {
    @Mock
    private PassengerRatingRepository passengerRatingRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PassengerRatingServiceImpl passengerRatingService;

    private Passenger passenger;
    private Exception exception;
    private AveragePassengerRatingResponse actualAveragePassengerRatingResponse;
    private AllPassengerRatingsResponse actualAllPassengerRatingsResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("Rating passenger with id {long} exists")
    public void ratingPassengerWithIdExists(long id) {
        passenger = TestPassengerUtil.getFirstPassenger();

        when(passengerRepository.findById(id))
                .thenReturn(Optional.of(passenger));

        Optional<Passenger> optionalPassenger = passengerRepository.findById(id);
        assertTrue(optionalPassenger.isPresent());
    }

    @Given("Rating passenger with id {long} not exists")
    public void ratingPassengerWithIdNotExists(long id) {
        passenger = TestPassengerUtil.getFirstPassenger();
        passenger.setId(id);

        when(passengerRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Passenger> passenger = passengerRepository.findById(id);
        assertFalse(passenger.isPresent());
    }

    @When("Method ratePassenger called with id {long}")
    public void methodDeletePassengerCalledWithId(long id) {
        PassengerRatingRequest passengerRatingRequest = TestPassengerRatingUtil.getPassengerRatingRequest();
        passengerRatingRequest.setPassengerId(id);

        try {
            passengerRatingService.ratePassenger(passengerRatingRequest);
        } catch (PassengerNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The PassengerNotFoundException should be thrown with the following message {string}")
    public void thePassengerNotFoundExceptionInRatePassengerMethodShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("Passenger with id {long} to retrieval all ratings exists")
    public void passengerWithIdToRetrievalAllRatingsExists(long id) {
        when(passengerRepository.existsById(id))
                .thenReturn(true);

        assertTrue(passengerRepository.existsById(id));
    }

    @When("Method getRatingsByPassengerId called with id {long}")
    public void methodGetRatingsByPassengerIdCalled(long id) {
        PassengerRating firstPassengerRating = TestPassengerRatingUtil.getFirstPassengerRating();
        PassengerRating secondPassengerRating = TestPassengerRatingUtil.getSecondPassengerRating();
        PassengerRatingResponse firstPassengerRatingResponse = TestPassengerRatingUtil.getFirstPassengerRatingResponse();
        PassengerRatingResponse secondPassengerRatingResponse = TestPassengerRatingUtil.getSecondPassengerRatingResponse();
        List<PassengerRating> passengerRatings = Arrays.asList(firstPassengerRating, secondPassengerRating);

        when(modelMapper.map(firstPassengerRating, PassengerRatingResponse.class))
                .thenReturn(firstPassengerRatingResponse);
        when(modelMapper.map(secondPassengerRating, PassengerRatingResponse.class))
                .thenReturn(secondPassengerRatingResponse);
        when(passengerRatingRepository.getPassengerRatingsByPassengerId(id))
                .thenReturn(passengerRatings);

        try {
            actualAllPassengerRatingsResponse = passengerRatingService.getRatingsByPassengerId(id);
        } catch (PassengerNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contains the list of passenger ratings")
    public void theResponseShouldContainsTheListOfPassengerWithIdRatings() {
        PassengerRatingResponse firstPassengerRatingResponse = TestPassengerRatingUtil.getFirstPassengerRatingResponse();
        PassengerRatingResponse secondPassengerRatingResponse = TestPassengerRatingUtil.getSecondPassengerRatingResponse();
        List<PassengerRatingResponse> expectedPassengerRatings = Arrays.asList(firstPassengerRatingResponse, secondPassengerRatingResponse);
        AllPassengerRatingsResponse expected = AllPassengerRatingsResponse.builder()
                .passengerRatings(expectedPassengerRatings)
                .build();

        assertEquals(expected, actualAllPassengerRatingsResponse);
    }

    @Given("Passenger with id {long} to retrieval average rating exists")
    public void passengerWithIdToRetrievalAverageRatingExists(long id) {
        when(passengerRepository.existsById(id))
                .thenReturn(true);

        assertTrue(passengerRepository.existsById(id));
    }

    @When("Method getAveragePassengerRating called with id {long}")
    public void methodGetAveragePassengerRatingCalled(long id) {
        PassengerRating firstPassengerRating = TestPassengerRatingUtil.getFirstPassengerRating();
        PassengerRating secondPassengerRating = TestPassengerRatingUtil.getSecondPassengerRating();
        List<PassengerRating> passengerRatings = Arrays.asList(firstPassengerRating, secondPassengerRating);

        when(passengerRatingRepository.getPassengerRatingsByPassengerId(id))
                .thenReturn(passengerRatings);

        try {
            actualAveragePassengerRatingResponse = passengerRatingService.getAveragePassengerRating(id);
        } catch (PassengerNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contains the average passenger with id {long} rating")
    public void theResponseShouldContainsTheAveragePassengerWithIdRating(long id) {
        Double expectedAverageRating = TestPassengerRatingUtil.getAveragePassengerRating();
        AveragePassengerRatingResponse expected =
                AveragePassengerRatingResponse.builder()
                        .averageRating(expectedAverageRating)
                        .passengerId(id)
                        .build();

        assertEquals(expected, actualAveragePassengerRatingResponse);
    }
}
