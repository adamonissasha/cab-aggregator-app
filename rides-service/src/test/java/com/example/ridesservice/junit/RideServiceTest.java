package com.example.ridesservice.junit;

import com.example.ridesservice.dto.message.RatingMessage;
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
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RideServiceTest {
    @Mock
    RideRepository rideRepository;
    @Mock
    PromoCodeService promoCodeService;
    @Mock
    PassengerWebClient passengerWebClient;
    @Mock
    StopService stopService;
    @Mock
    RideMapper rideMapper;
    @Mock
    DriverWebClient driverWebClient;
    @Mock
    BankWebClient bankWebClient;
    @Mock
    KafkaSendRatingGateway kafkaSendRatingGateway;
    @Mock
    Jedis jedis;
    @Mock
    ObjectMapper objectMapper;
    @InjectMocks
    RideServiceImpl rideService;

    @Test
    void testCreateRide_WithCardPaymentAndBankCardId_ShouldCreateRideSuccessfully() throws JsonProcessingException {
        CreateRideRequest createRideRequest = TestRideUtil.getCreateRideRequest();
        PromoCode promoCode = TestRideUtil.getFirstPromoCode();
        PassengerResponse passengerResponse = TestRideUtil.getPassengerResponse();
        DriverResponse driverResponse = TestRideUtil.getDriverResponse();
        Ride expectedRide = TestRideUtil.getFirstRide();
        List<StopResponse> stopResponses = TestRideUtil.getRideStopResponses();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stopResponses)
                .build();
        List<StopRequest> stopRequests = TestRideUtil.getRideStopRequests();
        PassengerRideResponse expected = TestRideUtil.getPassengerRideResponse();

        when(jedis.lpop(anyString()))
                .thenReturn("driverJson");
        when(objectMapper.readValue("driverJson", DriverResponse.class))
                .thenReturn(driverResponse);
        when(promoCodeService.getPromoCodeByName(createRideRequest.getPromoCode()))
                .thenReturn(promoCode);
        when(passengerWebClient.getPassenger(createRideRequest.getPassengerId()))
                .thenReturn(passengerResponse);
        when(rideService.getFreeDriver())
                .thenReturn(driverResponse);
        when(rideRepository.save(any(Ride.class)))
                .thenReturn(expectedRide);
        when(stopService.createStops(stopRequests, expectedRide))
                .thenReturn(stopsResponse);
        when(rideMapper.mapRideToPassengerRideResponse(expectedRide, stopResponses, driverResponse, driverResponse.getCar()))
                .thenReturn(expected);

        PassengerRideResponse actual = rideService.createRide(createRideRequest);

        assertEquals(expected, actual);

        verify(jedis, times(2))
                .lpop(anyString());
        verify(objectMapper, times(1))
                .readValue("driverJson", DriverResponse.class);
        verify(promoCodeService, times(1))
                .getPromoCodeByName(createRideRequest.getPromoCode());
        verify(passengerWebClient, times(1))
                .getPassenger(createRideRequest.getPassengerId());
        verify(rideRepository, times(1))
                .save(any());
        verify(stopService, times(1))
                .createStops(any(), any());
    }

    @Test
    void testCreateRide_WithCardPaymentAndNullBankCardId_ShouldThrowPaymentMethodException() {
        CreateRideRequest createRideRequest = TestRideUtil.getCreateRideRequest();
        createRideRequest.setBankCardId(null);

        assertThrows(PaymentMethodException.class, () -> rideService.createRide(createRideRequest));
    }

    @Test
    void testEditRide_WhenStatusNotCompletedOrCanceled_ShouldEditSuccessfully() {
        Long rideId = TestRideUtil.getFirstRideId();
        EditRideRequest editRideRequest = TestRideUtil.getEditRideRequest();
        Ride existingRide = TestRideUtil.getFirstRide();
        List<StopResponse> stopResponses = List.of();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stopResponses)
                .build();
        PassengerRideResponse passengerRideResponse = TestRideUtil.getPassengerRideResponse();
        DriverResponse driverResponse = TestRideUtil.getDriverResponse();
        PassengerRideResponse expected = TestRideUtil.getPassengerRideResponse();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));
        when(rideRepository.save(existingRide))
                .thenReturn(existingRide);
        when(driverWebClient.getDriver(anyLong()))
                .thenReturn(driverResponse);
        when(stopService.getRideStops(existingRide))
                .thenReturn(stopsResponse);
        when(rideMapper.mapRideToPassengerRideResponse(existingRide, stopResponses, driverResponse, driverResponse.getCar()))
                .thenReturn(passengerRideResponse);

        PassengerRideResponse actual = rideService.editRide(rideId, editRideRequest);

        assertEquals(expected, actual);

        verify(rideRepository, times(1))
                .save(existingRide);
        verify(stopService, times(2))
                .getRideStops(existingRide);
        verify(rideRepository, times(1))
                .findById(rideId);
        verify(driverWebClient, times(1))
                .getDriver(anyLong());
    }

    @Test
    void testEditRide_WhenStatusIsCompleted_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        EditRideRequest editRideRequest = TestRideUtil.getEditRideRequest();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.COMPLETED);

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.editRide(rideId, editRideRequest));

        verify(rideRepository, never())
                .save(any());
        verify(stopService, never())
                .editStops(anyList(), any());
        verify(rideRepository, times(1))
                .findById(rideId);
    }

    @Test
    void testEditRide_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();
        EditRideRequest editRideRequest = TestRideUtil.getEditRideRequest();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.editRide(rideId, editRideRequest));
    }

    @Test
    void testGetRideByRideId_WhenRideExists_ShouldReturnRideResponse() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        RideResponse expected = TestRideUtil.getFirstRideResponse();
        List<StopResponse> stops = TestRideUtil.getRideStopResponses();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stops)
                .build();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));
        when(stopService.getRideStops(existingRide))
                .thenReturn(stopsResponse);
        when(rideMapper.mapRideToRideResponse(existingRide, stops))
                .thenReturn(expected);

        RideResponse actual = rideService.getRideByRideId(rideId);

        assertEquals(expected, actual);

        verify(rideRepository, times(1))
                .findById(rideId);
        verify(stopService, times(1))
                .getRideStops(existingRide);
    }

    @Test
    void testGetRideByRideId_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.getRideByRideId(rideId));
    }

    @Test
    void testCancelRide_WhenRideStatusIsValid_ShouldCancelRide() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.CREATED);
        RideResponse expected = TestRideUtil.getFirstRideResponse();
        List<StopResponse> stops = TestRideUtil.getRideStopResponses();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stops)
                .build();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));
        when(stopService.getRideStops(existingRide))
                .thenReturn(stopsResponse);
        when(rideRepository.save(existingRide))
                .thenReturn(existingRide);
        when(rideMapper.mapRideToRideResponse(existingRide, stops))
                .thenReturn(expected);

        RideResponse actual = rideService.cancelRide(rideId);

        assertEquals(expected, actual);

        verify(rideRepository, times(1))
                .save(existingRide);
        verify(rideRepository, times(1))
                .findById(rideId);
        verify(stopService, times(1))
                .getRideStops(existingRide);
    }

    @Test
    void testCancelRide_WhenRideStatusIsNotValid_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.COMPLETED);

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.cancelRide(rideId));

        verify(rideRepository, times(1))
                .findById(rideId);
    }

    @Test
    void testCancelRide_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.cancelRide(rideId));
    }

    @Test
    void testStartRide_WhenRideStatusIsValid_ShouldStartRide() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.CREATED);
        RideResponse expected = TestRideUtil.getFirstRideResponse();
        List<StopResponse> stops = TestRideUtil.getRideStopResponses();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stops)
                .build();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));
        when(stopService.getRideStops(existingRide))
                .thenReturn(stopsResponse);
        when(rideRepository.save(existingRide))
                .thenReturn(existingRide);
        when(rideMapper.mapRideToRideResponse(existingRide, stops))
                .thenReturn(expected);

        RideResponse actual = rideService.startRide(rideId);

        assertEquals(expected, actual);

        verify(rideRepository, times(1))
                .save(existingRide);
        verify(rideRepository, times(1))
                .findById(rideId);
        verify(stopService, times(1))
                .getRideStops(existingRide);
    }

    @Test
    void testStartRide_WhenRideStatusIsNotValid_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.COMPLETED);

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.startRide(rideId));

        verify(rideRepository, times(1))
                .findById(rideId);
    }

    @Test
    void testStartRide_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.startRide(rideId));
    }

    @Test
    void testCompleteRide_WhenRideStatusIsValid_ShouldCompleteRide() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.STARTED);
        RideResponse expectedResponse = TestRideUtil.getFirstRideResponse();
        List<StopResponse> stops = TestRideUtil.getRideStopResponses();
        StopsResponse stopsResponse = StopsResponse.builder()
                .stops(stops)
                .build();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));
        when(stopService.getRideStops(existingRide))
                .thenReturn(stopsResponse);
        when(rideRepository.save(existingRide))
                .thenReturn(existingRide);
        when(rideMapper.mapRideToRideResponse(existingRide, stops))
                .thenReturn(expectedResponse);

        RideResponse result = rideService.completeRide(rideId);

        assertEquals(expectedResponse, result);

        verify(rideRepository, times(1))
                .findById(rideId);
        verify(stopService, times(1))
                .getRideStops(existingRide);
        verify(rideRepository, times(1))
                .save(existingRide);
        verify(driverWebClient, times(1))
                .changeDriverStatusToFree(anyLong());
        verify(bankWebClient, times(1))
                .refillDriverBankAccount(any());
    }

    @Test
    void testCompleteRide_WhenRideStatusIsNotValid_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.CANCELED);

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.completeRide(rideId));
    }

    @Test
    void testCompleteRide_WhenRideIsNotStarted_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.CREATED);

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.completeRide(rideId));

        verify(rideRepository, times(1))
                .findById(rideId);
    }

    @Test
    void testCompleteRide_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.completeRide(rideId));

        verify(rideRepository, times(1))
                .findById(rideId);
    }

    @Test
    void testRatePassenger_WhenRideStatusIsCompleted_ShouldSendPassengerRating() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.COMPLETED);
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();
        RatingMessage expectedRatingMessage = RatingMessage.builder()
                .rideId(rideId)
                .passengerId(existingRide.getPassengerId())
                .driverId(existingRide.getDriverId())
                .rating(ratingRequest.getRating())
                .build();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));

        rideService.ratePassenger(rideId, ratingRequest);

        verify(rideRepository, times(1))
                .findById(rideId);
        verify(kafkaSendRatingGateway, times(1))
                .sendPassengerRating(expectedRatingMessage);
    }

    @Test
    void testRatePassenger_WhenRideStatusIsNotCompleted_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();
        existingRide.setStatus(RideStatus.STARTED);

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.ratePassenger(rideId, ratingRequest));

        verify(rideRepository, times(1))
                .findById(rideId);
    }

    @Test
    void testRatePassenger_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.ratePassenger(rideId, ratingRequest));

        verify(rideRepository, times(1))
                .findById(rideId);
    }

    @Test
    void testRateDriver_WhenRideStatusIsCompleted_ShouldSendPassengerRating() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.COMPLETED);
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();
        RatingMessage expectedRatingMessage = RatingMessage.builder()
                .rideId(rideId)
                .passengerId(existingRide.getPassengerId())
                .driverId(existingRide.getDriverId())
                .rating(ratingRequest.getRating())
                .build();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));

        rideService.rateDriver(rideId, ratingRequest);

        verify(kafkaSendRatingGateway, times(1))
                .sendDriverRating(expectedRatingMessage);
        verify(rideRepository, times(1))
                .findById(rideId);
    }

    @Test
    void testRateDriver_WhenRideStatusIsNotCompleted_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();
        existingRide.setStatus(RideStatus.STARTED);

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.rateDriver(rideId, ratingRequest));

        verify(rideRepository, times(1))
                .findById(rideId);
    }

    @Test
    void testRateDriver_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.rateDriver(rideId, ratingRequest));

        verify(rideRepository, times(1))
                .findById(rideId);
    }
}