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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

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
    private Mono<AveragePassengerRatingResponse> actualAveragePassengerRatingResponse;
    private Mono<AllPassengerRatingsResponse> actualAllPassengerRatingsResponse;
    private Mono<Void> actualRating;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("Rating passenger with id {string} exists")
    public void ratingPassengerWithIdExists(String id) {
        passenger = TestPassengerUtil.getFirstPassenger();

        when(passengerRepository.findById(id))
                .thenReturn(Mono.just(passenger));

        StepVerifier.create(passengerRepository.findById(id))
                .expectNext(passenger)
                .verifyComplete();
    }

    @Given("Rating passenger with id {string} not exists")
    public void ratingPassengerWithIdNotExists(String id) {
        passenger = TestPassengerUtil.getFirstPassenger();
        passenger.setId(id);

        when(passengerRepository.findById(id))
                .thenReturn(Mono.empty());

        StepVerifier.create(passengerRepository.findById(id))
                .expectNextCount(0)
                .verifyComplete();
    }

    @When("Method ratePassenger called with id {string}")
    public void methodDeletePassengerCalledWithId(String id) {
        PassengerRatingRequest passengerRatingRequest = TestPassengerRatingUtil.getPassengerRatingRequest();
        passengerRatingRequest.setPassengerId(id);

        actualRating = passengerRatingService.ratePassenger(passengerRatingRequest);
    }

    @Then("The PassengerNotFoundException should be thrown with the following message {string}")
    public void thePassengerNotFoundExceptionInRatePassengerMethodShouldBeThrownWithMessage(String message) {
        StepVerifier.create(actualRating)
                .verifyErrorMatches(throwable ->
                        throwable instanceof PassengerNotFoundException
                                && throwable.getMessage().equals(message));
    }

    @Given("Passenger with id {string} to retrieval all ratings exists")
    public void passengerWithIdToRetrievalAllRatingsExists(String id) {
        passenger = TestPassengerUtil.getFirstPassenger();

        when(passengerRepository.findById(id))
                .thenReturn(Mono.just(passenger));

        StepVerifier.create(passengerRepository.findById(id))
                .expectNext(passenger)
                .verifyComplete();
    }

    @When("Method getRatingsByPassengerId called with id {string}")
    public void methodGetRatingsByPassengerIdCalled(String id) {
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
                .thenReturn(Flux.fromIterable(passengerRatings));

        actualAllPassengerRatingsResponse = passengerRatingService.getRatingsByPassengerId(id);
    }

    @Then("The response should contains the list of passenger ratings")
    public void theResponseShouldContainsTheListOfPassengerWithIdRatings() {
        PassengerRatingResponse firstPassengerRatingResponse = TestPassengerRatingUtil.getFirstPassengerRatingResponse();
        PassengerRatingResponse secondPassengerRatingResponse = TestPassengerRatingUtil.getSecondPassengerRatingResponse();
        List<PassengerRatingResponse> expectedPassengerRatings = Arrays.asList(firstPassengerRatingResponse, secondPassengerRatingResponse);
        AllPassengerRatingsResponse expected = AllPassengerRatingsResponse.builder()
                .passengerRatings(expectedPassengerRatings)
                .build();

        StepVerifier.create(actualAllPassengerRatingsResponse)
                .expectNext(expected)
                .verifyComplete();
    }

    @Then("The PassengerNotFoundException should be thrown in the getRatingsByPassengerId method with the following message {string}")
    public void thePassengerNotFoundExceptionShouldBeThrownInTheGetRatingsByPassengerIdMethodWithTheFollowingMessage(String message) {
        StepVerifier.create(actualAllPassengerRatingsResponse)
                .verifyErrorMatches(throwable ->
                        throwable instanceof PassengerNotFoundException
                                && throwable.getMessage().equals(message));
    }

    @Given("Passenger with id {string} to retrieval average rating exists")
    public void passengerWithIdToRetrievalAverageRatingExists(String id) {
        passenger = TestPassengerUtil.getFirstPassenger();

        when(passengerRepository.findById(id))
                .thenReturn(Mono.just(passenger));

        StepVerifier.create(passengerRepository.findById(id))
                .expectNext(passenger)
                .verifyComplete();
    }

    @When("Method getAveragePassengerRating called with id {string}")
    public void methodGetAveragePassengerRatingCalled(String id) {
        PassengerRating firstPassengerRating = TestPassengerRatingUtil.getFirstPassengerRating();
        PassengerRating secondPassengerRating = TestPassengerRatingUtil.getSecondPassengerRating();
        List<PassengerRating> passengerRatings = Arrays.asList(firstPassengerRating, secondPassengerRating);

        when(passengerRatingRepository.getPassengerRatingsByPassengerId(id))
                .thenReturn(Flux.fromIterable(passengerRatings));

        actualAveragePassengerRatingResponse = passengerRatingService.getAveragePassengerRating(id);
    }

    @Then("The response should contains the average passenger with id {string} rating")
    public void theResponseShouldContainsTheAveragePassengerWithIdRating(String id) {
        Double expectedAverageRating = TestPassengerRatingUtil.getAveragePassengerRating();
        AveragePassengerRatingResponse expected =
                AveragePassengerRatingResponse.builder()
                        .averageRating(expectedAverageRating)
                        .passengerId(id)
                        .build();

        StepVerifier.create(actualAveragePassengerRatingResponse)
                .expectNext(expected)
                .verifyComplete();
    }

    @Then("The PassengerNotFoundException should be thrown in the getAveragePassengerRating method with the following message {string}")
    public void thePassengerNotFoundExceptionShouldBeThrownInTheGetAveragePassengerRatingMethodWithTheFollowingMessage(String message) {
        StepVerifier.create(actualAveragePassengerRatingResponse)
                .verifyErrorMatches(throwable ->
                        throwable instanceof PassengerNotFoundException
                                && throwable.getMessage().equals(message));
    }
}
