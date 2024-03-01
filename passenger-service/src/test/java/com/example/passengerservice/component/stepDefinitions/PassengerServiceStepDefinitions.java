package com.example.passengerservice.component.stepDefinitions;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.exception.PhoneNumberUniqueException;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerRatingService;
import com.example.passengerservice.service.impl.PassengerServiceImpl;
import com.example.passengerservice.util.FieldValidator;
import com.example.passengerservice.util.TestPassengerUtil;
import com.example.passengerservice.webClient.BankWebClient;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
public class PassengerServiceStepDefinitions {
    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PassengerRatingService passengerRatingService;

    @Mock
    BankWebClient bankWebClient;

    @Mock
    FieldValidator fieldValidator;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private PassengerRequest passengerRequest;
    private Passenger passenger;
    private PassengerResponse expected;
    private Mono<PassengerResponse> actual;
    private Mono<Void> actualDeleting;
    private AveragePassengerRatingResponse passengerRating;
    private Mono<PassengerPageResponse> actualPageResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("New passenger has unique phone number {string}")
    public void givenPassengerWithUniquePhoneNumber(String uniquePhoneNumber) {
        passenger = TestPassengerUtil.getFirstPassenger();
        passenger.setPhoneNumber(uniquePhoneNumber);
        passengerRequest = TestPassengerUtil.getPassengerRequest();
        passengerRequest.setPhoneNumber(uniquePhoneNumber);
        passengerRating = TestPassengerUtil.getFirstPassengerRating();
        expected = TestPassengerUtil.getPassengerResponse();

        when(passengerRepository.findPassengerByPhoneNumber(uniquePhoneNumber))
                .thenReturn(Mono.empty());

        when(passengerRatingService.getAveragePassengerRating(anyString()))
                .thenReturn(Mono.just(passengerRating));
        when(modelMapper.map(any(PassengerRequest.class), eq(Passenger.class)))
                .thenReturn(passenger);
        when(modelMapper.map(passenger, PassengerResponse.class))
                .thenReturn(expected);
        when(passengerRepository.save(any(Passenger.class)))
                .thenReturn(Mono.just(passenger));

        StepVerifier.create(passengerRepository.findPassengerByPhoneNumber(uniquePhoneNumber))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Given("New passenger has existing phone number {string}")
    public void givenPassengerWithExistingPhoneNumber(String existingPhoneNumber) {
        passenger = TestPassengerUtil.getFirstPassenger();
        passenger.setPhoneNumber(existingPhoneNumber);
        passengerRequest = TestPassengerUtil.getPassengerRequest();
        passengerRequest.setPhoneNumber(existingPhoneNumber);

        when(passengerRepository.findPassengerByPhoneNumber(existingPhoneNumber))
                .thenReturn(Mono.just(passenger));

        StepVerifier.create(passengerRepository.findPassengerByPhoneNumber(existingPhoneNumber))
                .expectNext(passenger)
                .verifyComplete();
    }

    @When("Method createPassenger called")
    public void whenCreatePassengerMethodCalled() {
        actual = passengerService.createPassenger(passengerRequest);
    }

    @Then("The response should contain the details of the created passenger")
    public void thenResponseShouldContainCreatedPassengerDetails() {
        StepVerifier.create(actual)
                .expectNext(expected)
                .verifyComplete();
    }

    @Then("The PhoneNumberUniqueException should be thrown with message {string}")
    public void thenPhoneNumberUniqueExceptionShouldBeThrownWithMessage(String message) {
        StepVerifier.create(actual)
                .verifyErrorMatches(throwable ->
                        throwable instanceof PhoneNumberUniqueException
                                && throwable.getMessage().equals(message));
    }

    @Given("Editing passenger with id {string} exists and phone number {string} unique")
    public void editingPassengerWithIdExistsAndPhoneNumberUnique(String id, String phoneNumber) {
        passengerRequest = TestPassengerUtil.getPassengerRequest();
        passenger = TestPassengerUtil.getFirstPassenger();
        expected = TestPassengerUtil.getPassengerResponse();
        passengerRating = TestPassengerUtil.getFirstPassengerRating();

        when(passengerRepository.findById(id))
                .thenReturn(Mono.just(passenger));
        when(passengerRepository.findPassengerByPhoneNumber(phoneNumber))
                .thenReturn(Mono.empty());
        when(modelMapper.map(passengerRequest, Passenger.class))
                .thenReturn(passenger);
        when(passengerRepository.save(passenger))
                .thenReturn(Mono.just(passenger));
        when(passengerRatingService.getAveragePassengerRating(anyString()))
                .thenReturn(Mono.just(passengerRating));
        when(modelMapper.map(passenger, PassengerResponse.class))
                .thenReturn(expected);

        StepVerifier.create(passengerRepository.findPassengerByPhoneNumber(phoneNumber))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Given("Editing passenger with id {string} has existing phone number {string}")
    public void editingPassengerHasExistingPhoneNumber(String id, String existingPhoneNumber) {
        passenger = TestPassengerUtil.getFirstPassenger();
        Passenger existingPassenger = TestPassengerUtil.getSecondPassenger();
        existingPassenger.setPhoneNumber(existingPhoneNumber);
        passengerRequest = TestPassengerUtil.getPassengerRequest();
        passengerRequest.setPhoneNumber(existingPhoneNumber);

        when(passengerRepository.findById(id))
                .thenReturn(Mono.just(passenger));
        when(passengerRepository.findPassengerByPhoneNumber(passengerRequest.getPhoneNumber()))
                .thenReturn(Mono.just(existingPassenger));

        StepVerifier.create(passengerRepository.findPassengerByPhoneNumber(existingPhoneNumber))
                .expectNext(existingPassenger)
                .verifyComplete();
    }

    @Given("There is no passenger with id {string}")
    public void givenPassengerWithIdNotExists(String id) {
        passenger = TestPassengerUtil.getFirstPassenger();
        passenger.setId(id);

        when(passengerRepository.findById(id))
                .thenReturn(Mono.empty());

        StepVerifier.create(passengerRepository.findById(id))
                .expectNextCount(0)
                .verifyComplete();
    }

    @When("Method editPassenger called with id {string}")
    public void methodEditPassengerCalled(String id) {
        actual = passengerService.editPassenger(id, passengerRequest);
    }

    @Then("The response should contain the details of the edited passenger")
    public void theResponseShouldContainTheDetailsOfTheEditedPassenger() {
        StepVerifier.create(actual)
                .expectNext(expected)
                .verifyComplete();
    }

    @Then("The PassengerNotFoundException should be thrown with message {string}")
    public void thenPassengerNotFoundExceptionShouldBeThrownWithMessage(String message) {
        StepVerifier.create(actual)
                .verifyErrorMatches(throwable ->
                        throwable instanceof PassengerNotFoundException
                                && throwable.getMessage().equals(message));
    }

    @Given("There is a passenger with id {string}")
    public void givenPassengerWithIdExists(String id) {
        passenger = TestPassengerUtil.getFirstPassenger();
        expected = TestPassengerUtil.getPassengerResponse();
        passengerRating = TestPassengerUtil.getFirstPassengerRating();

        when(passengerRepository.findById(id))
                .thenReturn(Mono.just(passenger));
        when(passengerRatingService.getAveragePassengerRating(anyString()))
                .thenReturn(Mono.just(passengerRating));
        when(modelMapper.map(passenger, PassengerResponse.class))
                .thenReturn(expected);

        StepVerifier.create(passengerRepository.findById(id))
                .expectNext(passenger)
                .verifyComplete();
    }

    @When("Method getPassengerById called with id {string}")
    public void whenGetPassengerByIdMethodCalled(String id) {
        actual = passengerService.getPassengerById(id);
    }

    @Then("The response should contain the details of passenger with id {string}")
    public void thenResponseShouldContainPassengerDetails(String id) {
        StepVerifier.create(actual)
                .expectNext(expected)
                .verifyComplete();
    }

    @Given("Deleting passenger with id {string} exists")
    public void deletingPassengerWithIdExists(String id) {
        passenger = TestPassengerUtil.getFirstPassenger();

        when(passengerRepository.findById(id))
                .thenReturn(Mono.just(passenger));

        StepVerifier.create(passengerRepository.findById(id))
                .expectNext(passenger)
                .verifyComplete();
    }

    @When("Method deletePassengerById called with id {string}")
    public void methodDeletePassengerCalledWithId(String id) {
        actualDeleting = passengerService.deletePassengerById(id);
    }

    @Then("The PassengerNotFoundException should be thrown after deleting with message {string}")
    public void thePassengerNotFoundExceptionShouldBeThrownAfterDeletingWithMessage(String message) {
        StepVerifier.create(actualDeleting)
                .verifyErrorMatches(throwable ->
                        throwable instanceof PassengerNotFoundException
                                && throwable.getMessage().equals(message));
    }

    @Given("There are passengers in the system in page {int} with size {int} and sort by {string}")
    public void thereArePassengersInTheSystem(int page, int size, String sortBy) {
        List<Passenger> passengers = TestPassengerUtil.getPassengers();
        List<PassengerResponse> passengerResponses = TestPassengerUtil.getPassengerResponses();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());

        when(fieldValidator.checkSortField(eq(Passenger.class), eq(sortBy)))
                .thenReturn(Mono.empty());
        when(passengerRepository.findAllByIsActiveTrue(any(Pageable.class)))
                .thenReturn(Flux.fromIterable(passengers));
        when(modelMapper.map(passengers.get(0), PassengerResponse.class))
                .thenReturn(passengerResponses.get(0));
        when(modelMapper.map(passengers.get(1), PassengerResponse.class))
                .thenReturn(passengerResponses.get(1));
        when(passengerRepository.countAllByIsActiveTrue())
                .thenReturn(Mono.just(2L));
        when(passengerRatingService.getAveragePassengerRating(TestPassengerUtil.getFirstPassengerId()))
                .thenReturn(Mono.just(TestPassengerUtil.getFirstPassengerRating()));
        when(passengerRatingService.getAveragePassengerRating(TestPassengerUtil.getSecondPassengerId()))
                .thenReturn(Mono.just(TestPassengerUtil.getSecondPassengerRating()));

        Flux<Passenger> passengerPage = passengerRepository.findAllByIsActiveTrue(pageable);
        assertEquals(Boolean.TRUE, passengerPage.hasElements().block());
    }

    @When("Method getAllPassengers called with page {int}, size {int}, and sort by {string}")
    public void methodGetAllPassengersCalledWithPageSizeAndSortBy(int page, int size, String sortBy) {
        actualPageResponse = passengerService.getAllPassengers(page, size, sortBy);
    }

    @Then("The response should contain a page of passengers number {int} with size {int}")
    public void theResponseShouldContainAListOfPassengers(int page, int size) {
        List<PassengerResponse> passengerResponses = TestPassengerUtil.getPassengerResponses();

        PassengerPageResponse expectedPageResponse = PassengerPageResponse.builder()
                .passengers(passengerResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(2)
                .totalPages(1)
                .build();

        StepVerifier.create(actualPageResponse)
                .expectNext(expectedPageResponse)
                .verifyComplete();
    }
}
