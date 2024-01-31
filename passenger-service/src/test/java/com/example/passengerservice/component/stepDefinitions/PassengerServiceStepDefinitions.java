package com.example.passengerservice.component.stepDefinitions;

import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerPageResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.exception.IncorrectFieldNameException;
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
import static org.mockito.Mockito.when;

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
    private PassengerResponse actual;
    private Exception exception;
    private AveragePassengerRatingResponse passengerRating;
    private PassengerPageResponse actualPageResponse;

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
                .thenReturn(Optional.empty());

        when(passengerRatingService.getAveragePassengerRating(anyLong()))
                .thenReturn(passengerRating);
        when(modelMapper.map(any(PassengerRequest.class), eq(Passenger.class)))
                .thenReturn(passenger);
        when(modelMapper.map(passenger, PassengerResponse.class))
                .thenReturn(expected);
        when(passengerRepository.save(any(Passenger.class)))
                .thenReturn(passenger);

        Optional<Passenger> optionalPassenger = passengerRepository.findPassengerByPhoneNumber(uniquePhoneNumber);
        assertFalse(optionalPassenger.isPresent());
    }

    @Given("New passenger has existing phone number {string}")
    public void givenPassengerWithExistingPhoneNumber(String existingPhoneNumber) {
        passenger = TestPassengerUtil.getFirstPassenger();
        passenger.setPhoneNumber(existingPhoneNumber);
        passengerRequest = TestPassengerUtil.getPassengerRequest();
        passengerRequest.setPhoneNumber(existingPhoneNumber);

        when(passengerRepository.findPassengerByPhoneNumber(existingPhoneNumber))
                .thenReturn(Optional.of(passenger));

        Optional<Passenger> optionalPassenger = passengerRepository.findPassengerByPhoneNumber(existingPhoneNumber);
        assertTrue(optionalPassenger.isPresent());
    }

    @When("Method createPassenger called")
    public void whenCreatePassengerMethodCalled() {
        try {
            actual = passengerService.createPassenger(passengerRequest);
        } catch (PhoneNumberUniqueException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the created passenger")
    public void thenResponseShouldContainCreatedPassengerDetails() {
        passenger = passengerRepository.save(passenger);
        expected = passengerService.mapPassengerToPassengerResponse(passenger);

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The PhoneNumberUniqueException should be thrown with message {string}")
    public void thenPhoneNumberUniqueExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("Editing passenger with id {long} exists and phone number {string} unique")
    public void editingPassengerWithIdExistsAndPhoneNumberUnique(long id, String phoneNumber) {
        passengerRequest = TestPassengerUtil.getPassengerRequest();
        passenger = TestPassengerUtil.getFirstPassenger();
        expected = TestPassengerUtil.getPassengerResponse();
        passengerRating = TestPassengerUtil.getFirstPassengerRating();

        when(passengerRepository.findById(id))
                .thenReturn(Optional.of(passenger));
        when(passengerRepository.findPassengerByPhoneNumber(phoneNumber))
                .thenReturn(Optional.empty());
        when(modelMapper.map(passengerRequest, Passenger.class))
                .thenReturn(passenger);
        when(passengerRepository.save(passenger))
                .thenReturn(passenger);
        when(passengerRatingService.getAveragePassengerRating(anyLong()))
                .thenReturn(passengerRating);
        when(modelMapper.map(passenger, PassengerResponse.class))
                .thenReturn(expected);

        Optional<Passenger> optionalPassenger = passengerRepository.findPassengerByPhoneNumber(phoneNumber);
        assertFalse(optionalPassenger.isPresent());
    }

    @Given("Editing passenger with id {long} has existing phone number {string}")
    public void editingPassengerHasExistingPhoneNumber(long id, String existingPhoneNumber) {
        passenger = TestPassengerUtil.getFirstPassenger();
        passenger.setPhoneNumber(existingPhoneNumber);
        passengerRequest = TestPassengerUtil.getPassengerRequest();
        passengerRequest.setPhoneNumber(existingPhoneNumber);

        when(passengerRepository.findById(id))
                .thenReturn(Optional.of(passenger));
        when(passengerRepository.findPassengerByPhoneNumber(existingPhoneNumber))
                .thenReturn(Optional.of(passenger));

        Optional<Passenger> optionalPassenger = passengerRepository.findPassengerByPhoneNumber(existingPhoneNumber);
        assertTrue(optionalPassenger.isPresent());
    }

    @Given("There is no passenger with id {long}")
    public void givenPassengerWithIdNotExists(long id) {
        passenger = TestPassengerUtil.getFirstPassenger();
        passenger.setId(id);

        when(passengerRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Passenger> passenger = passengerRepository.findById(id);
        assertFalse(passenger.isPresent());
    }

    @When("Method editPassenger called with id {long}")
    public void methodEditPassengerCalled(long id) {
        try {
            actual = passengerService.editPassenger(id, passengerRequest);
        } catch (PhoneNumberUniqueException | PassengerNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the edited passenger")
    public void theResponseShouldContainTheDetailsOfTheEditedPassenger() {
        passenger = passengerRepository.save(passenger);
        expected = passengerService.mapPassengerToPassengerResponse(passenger);

        assertThat(actual).isEqualTo(expected);
    }

    @Then("The PassengerNotFoundException should be thrown with message {string}")
    public void thenPassengerNotFoundExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("There is a passenger with id {long}")
    public void givenPassengerWithIdExists(long id) {
        passenger = TestPassengerUtil.getFirstPassenger();
        expected = TestPassengerUtil.getPassengerResponse();
        passengerRating = TestPassengerUtil.getFirstPassengerRating();

        when(passengerRepository.findById(id))
                .thenReturn(Optional.of(passenger));
        when(passengerRatingService.getAveragePassengerRating(anyLong()))
                .thenReturn(passengerRating);
        when(modelMapper.map(passenger, PassengerResponse.class))
                .thenReturn(expected);

        assertTrue(passengerRepository.findById(id).isPresent());
    }

    @When("Method getPassengerById called with id {long}")
    public void whenGetPassengerByIdMethodCalled(long id) {
        try {
            actual = passengerService.getPassengerById(id);
        } catch (PassengerNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of passenger with id {long}")
    public void thenResponseShouldContainPassengerDetails(long id) {
        passenger = passengerRepository.findById(id).get();
        expected = passengerService.mapPassengerToPassengerResponse(passenger);

        assertThat(actual).isEqualTo(expected);
    }

    @Given("Deleting passenger with id {long} exists")
    public void deletingPassengerWithIdExists(long id) {
        passenger = TestPassengerUtil.getFirstPassenger();

        when(passengerRepository.findById(id))
                .thenReturn(Optional.of(passenger));

        assertTrue(passengerRepository.findById(id).isPresent());
    }

    @When("Method deletePassengerById called with id {long}")
    public void methodDeletePassengerCalledWithId(long id) {
        try {
            passengerService.deletePassengerById(id);
        } catch (PassengerNotFoundException ex) {
            exception = ex;
        }
    }

    @Given("There are passengers in the system in page {int} with size {int} and sort by {string}")
    public void thereArePassengersInTheSystem(int page, int size, String sortBy) {
        List<Passenger> passengers = TestPassengerUtil.getPassengers();
        List<PassengerResponse> passengerResponses = TestPassengerUtil.getPassengerResponses();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Passenger> mockPassengerPage = new PageImpl<>(passengers, pageable, passengers.size());

        when(passengerRepository.findAll(any(Pageable.class)))
                .thenReturn(mockPassengerPage);
        when(modelMapper.map(passengers.get(0), PassengerResponse.class))
                .thenReturn(passengerResponses.get(0));
        when(modelMapper.map(passengers.get(1), PassengerResponse.class))
                .thenReturn(passengerResponses.get(1));

        Page<Passenger> passengerPage = passengerRepository.findAll(pageable);
        assertTrue(passengerPage.hasContent());
    }

    @When("Method getAllPassengers called with page {int}, size {int}, and sort by {string}")
    public void methodGetAllPassengersCalledWithPageSizeAndSortBy(int page, int size, String sortBy) {
        when(passengerRatingService.getAveragePassengerRating(TestPassengerUtil.getFirstPassengerId()))
                .thenReturn(TestPassengerUtil.getFirstPassengerRating());
        when(passengerRatingService.getAveragePassengerRating(TestPassengerUtil.getSecondPassengerId()))
                .thenReturn(TestPassengerUtil.getSecondPassengerRating());

        try {
            actualPageResponse = passengerService.getAllPassengers(page, size, sortBy);
        } catch (IncorrectFieldNameException ex) {
            exception = ex;
        }
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

        assertEquals(expectedPageResponse, actualPageResponse);
    }
}
