package com.example.ridesservice.component.stepDefinitions;


import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.request.EditRideRequest;
import com.example.ridesservice.dto.request.RatingRequest;
import com.example.ridesservice.dto.request.StopRequest;
import com.example.ridesservice.dto.response.DriverResponse;
import com.example.ridesservice.dto.response.PassengerResponse;
import com.example.ridesservice.dto.response.PassengerRideResponse;
import com.example.ridesservice.dto.response.RideResponse;
import com.example.ridesservice.dto.response.StopResponse;
import com.example.ridesservice.dto.response.StopsResponse;
import com.example.ridesservice.exception.PaymentMethodException;
import com.example.ridesservice.exception.RideNotFoundException;
import com.example.ridesservice.exception.RideStatusException;
import com.example.ridesservice.kafka.service.KafkaSendRatingGateway;
import com.example.ridesservice.mapper.RideMapper;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.RideStatus;
import com.example.ridesservice.repository.RideRepository;
import com.example.ridesservice.service.PromoCodeService;
import com.example.ridesservice.service.StopService;
import com.example.ridesservice.service.impl.RideServiceImpl;
import com.example.ridesservice.util.TestRideUtil;
import com.example.ridesservice.webClient.BankWebClient;
import com.example.ridesservice.webClient.DriverWebClient;
import com.example.ridesservice.webClient.PassengerWebClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class RideServiceStepDefinitions {
    @Mock
    private RideRepository rideRepository;

    @Mock
    BankWebClient bankWebClient;

    @Mock
    PassengerWebClient passengerWebClient;

    @Mock
    DriverWebClient driverWebClient;

    @Mock
    private RideMapper rideMapper;

    @Mock
    Jedis jedis;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    PromoCodeService promoCodeService;

    @Mock
    StopService stopService;

    @Mock
    KafkaSendRatingGateway kafkaSendRatingGateway;

    @InjectMocks
    private RideServiceImpl rideService;

    private CreateRideRequest createRideRequest;
    private EditRideRequest editRideRequest;
    private RatingRequest ratingRequest;
    private Ride ride;
    private PassengerRideResponse expectedPassengerRideResponse;
    private PassengerRideResponse actualPassengerRideResponse;
    private RideResponse expectedRideResponse;
    private RideResponse actualRideResponse;
    private Exception exception;
    private DriverResponse driver;
    private List<StopResponse> stops;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("New ride with valid payment data")
    public void newRideWithValidPaymentData() throws JsonProcessingException {
        createRideRequest = TestRideUtil.getCreateRideRequest();
        PromoCode promoCode = TestRideUtil.getFirstPromoCode();
        PassengerResponse passengerResponse = TestRideUtil.getPassengerResponse();
        driver = TestRideUtil.getDriverResponse();
        ride = TestRideUtil.getFirstRide();
        stops = TestRideUtil.getRideStopResponses();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stops)
                .build();
        List<StopRequest> stopRequests = TestRideUtil.getRideStopRequests();
        expectedPassengerRideResponse = TestRideUtil.getPassengerRideResponse();

        when(jedis.lpop(anyString()))
                .thenReturn("driverJson");
        when(objectMapper.readValue("driverJson", DriverResponse.class))
                .thenReturn(driver);
        when(promoCodeService.getPromoCodeByName(createRideRequest.getPromoCode()))
                .thenReturn(promoCode);
        when(passengerWebClient.getPassenger(createRideRequest.getPassengerId()))
                .thenReturn(passengerResponse);
        when(rideService.getFreeDriver())
                .thenReturn(driver);
        when(rideRepository.save(any(Ride.class)))
                .thenReturn(ride);
        when(stopService.createStops(stopRequests, ride))
                .thenReturn(stopsResponse);
        when(rideMapper.mapRideToPassengerRideResponse(ride, stops, driver, driver.getCar()))
                .thenReturn(expectedPassengerRideResponse);
    }

    @Given("New ride with payment method {string} and not selected card")
    public void newRideWithPaymentMethodAndNotSelectedCard(String paymentMethod) {
        createRideRequest = TestRideUtil.getCreateRideRequest();
        createRideRequest.setPaymentMethod(paymentMethod);
        createRideRequest.setBankCardId(null);
    }

    @When("Method createRide called")
    public void methodCreateRideCalled() {
        try {
            actualPassengerRideResponse = rideService.createRide(createRideRequest);
        } catch (PaymentMethodException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the created ride")
    public void theResponseShouldContainTheDetailsOfTheCreatedRide() {
        ride = rideRepository.save(ride);
        expectedPassengerRideResponse = rideMapper.mapRideToPassengerRideResponse(ride, stops, driver, driver.getCar());

        assertThat(actualPassengerRideResponse).isEqualTo(expectedPassengerRideResponse);
    }

    @Then("The PaymentMethodException should be thrown with message {string}")
    public void thePaymentMethodExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("There is a ride with id {long}")
    public void thereIsARideWithId(long id) {
        ride = TestRideUtil.getFirstRide();
        expectedRideResponse = TestRideUtil.getFirstRideResponse();
        stops = TestRideUtil.getRideStopResponses();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stops)
                .build();

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));
        when(stopService.getRideStops(ride))
                .thenReturn(stopsResponse);
        when(rideMapper.mapRideToRideResponse(ride, stops))
                .thenReturn(expectedRideResponse);

        assertTrue(rideRepository.findById(id).isPresent());
    }

    @Given("There is no ride with id {long}")
    public void thereIsNoRideWithId(long id) {
        Long rideId = TestRideUtil.getFirstRideId();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.empty());

        assertFalse(rideRepository.findById(id).isPresent());
    }

    @When("Method getRideById called with id {long}")
    public void methodGetRideByIdCalledWithId(long id) {
        try {
            actualRideResponse = rideService.getRideByRideId(id);
        } catch (RideNotFoundException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of ride with id {long}")
    public void theResponseShouldContainTheDetailsOfRideWithId(long id) {
        ride = rideRepository.findById(id).get();
        expectedRideResponse = rideMapper.mapRideToRideResponse(ride, stops);

        assertThat(actualRideResponse).isEqualTo(expectedRideResponse);
    }

    @Then("The RideNotFoundException should be thrown with message {string}")
    public void theRideNotFoundExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("Editing ride with id {int} exists and status correct")
    public void editingRideWithIdExistsAndStatusNotOr(long id) {
        editRideRequest = TestRideUtil.getEditRideRequest();
        ride = TestRideUtil.getFirstRide();
        stops = List.of();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stops)
                .build();
        expectedPassengerRideResponse = TestRideUtil.getPassengerRideResponse();
        driver = TestRideUtil.getDriverResponse();

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));
        when(rideRepository.save(ride))
                .thenReturn(ride);
        when(driverWebClient.getDriver(anyLong()))
                .thenReturn(driver);
        when(stopService.getRideStops(ride))
                .thenReturn(stopsResponse);
        when(rideMapper.mapRideToPassengerRideResponse(ride, stops, driver, driver.getCar()))
                .thenReturn(expectedPassengerRideResponse);

        assertTrue(rideRepository.findById(id).isPresent());
    }

    @Given("Editing ride with id {int} has invalid status {string}")
    public void editingRideWithIdHasInvalidStatus(long id, String statusCanceled) {
        editRideRequest = TestRideUtil.getEditRideRequest();
        ride = TestRideUtil.getFirstRide();
        ride.setId(id);
        ride.setStatus(RideStatus.valueOf(statusCanceled));

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));

        assertTrue(rideRepository.findById(id).isPresent());
    }

    @When("Method editRide called with id {int}")
    public void methodEditRideCalledWithId(long id) {
        try {
            actualPassengerRideResponse = rideService.editRide(id, editRideRequest);
        } catch (RideNotFoundException | RideStatusException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the edited ride")
    public void theResponseShouldContainTheDetailsOfTheEditedRide() {
        ride = rideRepository.save(ride);
        expectedPassengerRideResponse = rideMapper.mapRideToPassengerRideResponse(ride, stops, driver, driver.getCar());

        assertThat(actualPassengerRideResponse).isEqualTo(expectedPassengerRideResponse);
    }

    @Then("The RideStatusException should be thrown with message {string}")
    public void theRideStatusExceptionShouldBeThrownWithMessage(String message) {
        assertEquals(message, exception.getMessage());
    }

    @Given("Canceled ride with id {long} has correct status")
    public void canceledRideWithIdHasCorrectStatus(long id) {
        ride = TestRideUtil.getFirstRide();
        ride.setStatus(RideStatus.CREATED);
        expectedRideResponse = TestRideUtil.getFirstRideResponse();
        stops = TestRideUtil.getRideStopResponses();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stops)
                .build();

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));
        when(stopService.getRideStops(ride))
                .thenReturn(stopsResponse);
        when(rideRepository.save(ride))
                .thenReturn(ride);
        when(rideMapper.mapRideToRideResponse(ride, stops))
                .thenReturn(expectedRideResponse);

        assertTrue(rideRepository.findById(id).isPresent());
    }

    @Given("Canceled ride with id {long} has incorrect status {string}")
    public void canceledRideWithIdHasIncorrectStatus(long id, String statusCompleted) {
        ride = TestRideUtil.getFirstRide();
        ride.setStatus(RideStatus.valueOf(statusCompleted));
        ride.setId(id);

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));

        assertTrue(rideRepository.findById(id).isPresent());
    }

    @When("Method cancelRide called with id {long}")
    public void methodCancelRideCalledWithId(long id) {
        try {
            actualRideResponse = rideService.cancelRide(id);
        } catch (RideNotFoundException | RideStatusException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the canceled ride")
    public void theResponseShouldContainTheDetailsOfTheCanceledRide() {
        ride = rideRepository.save(ride);
        expectedRideResponse = rideMapper.mapRideToRideResponse(ride, stops);

        assertThat(actualRideResponse).isEqualTo(expectedRideResponse);
    }

    @Given("Started ride with id {long} has correct status {string}")
    public void startedRideWithIdHasCorrectStatus(long id, String statusCreated) {
        ride = TestRideUtil.getFirstRide();
        ride.setStatus(RideStatus.valueOf(statusCreated));
        expectedRideResponse = TestRideUtil.getFirstRideResponse();
        stops = TestRideUtil.getRideStopResponses();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stops)
                .build();

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));
        when(stopService.getRideStops(ride))
                .thenReturn(stopsResponse);
        when(rideRepository.save(ride))
                .thenReturn(ride);
        when(rideMapper.mapRideToRideResponse(ride, stops))
                .thenReturn(expectedRideResponse);

        assertTrue(rideRepository.findById(id).isPresent());
    }

    @Given("Started ride with id {long} has incorrect status {string}")
    public void startedRideWithIdHasIncorrectStatus(long id, String statusCompleted) {
        ride = TestRideUtil.getFirstRide();
        ride.setStatus(RideStatus.valueOf(statusCompleted));
        ride.setId(id);

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));

        assertTrue(rideRepository.findById(id).isPresent());
    }

    @When("Method startRide called with id {long}")
    public void methodStartRideCalledWithId(long id) {
        try {
            actualRideResponse = rideService.startRide(id);
        } catch (RideNotFoundException | RideStatusException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the started ride")
    public void theResponseShouldContainTheDetailsOfTheStartedRide() {
        ride = rideRepository.save(ride);
        expectedRideResponse = rideMapper.mapRideToRideResponse(ride, stops);

        assertThat(actualRideResponse).isEqualTo(expectedRideResponse);
    }

    @Given("Completed ride with id {long} has correct status {string}")
    public void completedRideWithIdHasCorrectStatus(long id, String statusStarted) {
        ride = TestRideUtil.getFirstRide();
        ride.setStatus(RideStatus.valueOf(statusStarted));
        expectedRideResponse = TestRideUtil.getFirstRideResponse();
        stops = TestRideUtil.getRideStopResponses();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stops)
                .build();

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));
        when(stopService.getRideStops(ride))
                .thenReturn(stopsResponse);
        when(rideRepository.save(ride))
                .thenReturn(ride);
        when(rideMapper.mapRideToRideResponse(ride, stops))
                .thenReturn(expectedRideResponse);

        assertTrue(rideRepository.findById(id).isPresent());
    }

    @Given("Completed ride with id {long} has incorrect status {string}")
    public void completedRideWithIdHasIncorrectStatus(long id, String statusCanceled) {
        ride = TestRideUtil.getFirstRide();
        ride.setStatus(RideStatus.valueOf(statusCanceled));
        ride.setId(id);

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));

        assertTrue(rideRepository.findById(id).isPresent());
    }

    @Given("Completed ride with id {long} not started")
    public void completedRideWithIdNotStarted(long id) {
        ride = TestRideUtil.getFirstRide();
        ride.setStatus(RideStatus.CREATED);
        ride.setId(id);

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));

        assertTrue(rideRepository.findById(id).isPresent());
        assertNotEquals(ride.getStatus(), RideStatus.STARTED);
    }

    @When("Method completeRide called with id {long}")
    public void methodCompleteRideCalledWithId(long id) {
        try {
            actualRideResponse = rideService.completeRide(id);
        } catch (RideNotFoundException | RideStatusException ex) {
            exception = ex;
        }
    }

    @Then("The response should contain the details of the completed ride")
    public void theResponseShouldContainTheDetailsOfTheCompletedRide() {
        ride = rideRepository.save(ride);
        expectedRideResponse = rideMapper.mapRideToRideResponse(ride, stops);

        assertThat(actualRideResponse).isEqualTo(expectedRideResponse);
    }

    @Given("Ride with id {long} is completed")
    public void rideWithIdIsCompleted(long id) {
        ride = TestRideUtil.getFirstRide();
        ride.setStatus(RideStatus.COMPLETED);
        ratingRequest = TestRideUtil.getRatingRequest();

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));

        assertTrue(rideRepository.findById(id).isPresent());
        assertEquals(ride.getStatus(), RideStatus.COMPLETED);
    }

    @Given("Ride with id {long} isn't completed")
    public void rideWithIdIsnTCompleted(long id) {
        ride = TestRideUtil.getFirstRide();
        ratingRequest = TestRideUtil.getRatingRequest();
        ride.setStatus(RideStatus.STARTED);
        ride.setId(id);

        when(rideRepository.findById(id))
                .thenReturn(Optional.of(ride));

        assertTrue(rideRepository.findById(id).isPresent());
        assertNotEquals(ride.getStatus(), RideStatus.COMPLETED);
    }

    @When("Method ratePassenger called with id {long}")
    public void methodRatePassengerCalledWithId(long id) {
        try {
            rideService.ratePassenger(id, ratingRequest);
        } catch (RideNotFoundException | RideStatusException ex) {
            exception = ex;
        }
    }

    @When("Method rateDriver called with id {long}")
    public void methodRateDriverCalledWithId(long id) {
        try {
            rideService.rateDriver(id, ratingRequest);
        } catch (RideNotFoundException | RideStatusException ex) {
            exception = ex;
        }
    }
}
