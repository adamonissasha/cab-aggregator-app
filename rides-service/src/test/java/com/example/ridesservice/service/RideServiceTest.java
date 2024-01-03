package com.example.ridesservice.service;

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
import com.example.ridesservice.exception.PaymentMethodException;
import com.example.ridesservice.exception.RideNotFoundException;
import com.example.ridesservice.exception.RideStatusException;
import com.example.ridesservice.kafka.service.KafkaSendRatingGateway;
import com.example.ridesservice.mapper.RideMapper;
import com.example.ridesservice.model.PromoCode;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.RideStatus;
import com.example.ridesservice.repository.RideRepository;
import com.example.ridesservice.service.impl.RideServiceImpl;
import com.example.ridesservice.util.FieldValidator;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    FieldValidator fieldValidator;
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
        List<StopRequest> stopRequests = TestRideUtil.getRideStopRequests();
        PassengerRideResponse expectedResponse = TestRideUtil.getPassengerRideResponse();

        when(jedis.lpop(anyString())).thenReturn("driverJson");
        when(objectMapper.readValue("driverJson", DriverResponse.class)).thenReturn(driverResponse);
        when(promoCodeService.getPromoCodeByName(createRideRequest.getPromoCode())).thenReturn(promoCode);
        when(passengerWebClient.getPassenger(createRideRequest.getPassengerId())).thenReturn(passengerResponse);
        when(rideService.getFreeDriver()).thenReturn(driverResponse);
        when(rideRepository.save(any(Ride.class))).thenReturn(expectedRide);
        when(stopService.createStops(stopRequests, expectedRide)).thenReturn(stopResponses);
        when(rideMapper.mapRideToPassengerRideResponse(expectedRide, stopResponses, driverResponse, driverResponse.getCar()))
                .thenReturn(expectedResponse);

        PassengerRideResponse result = rideService.createRide(createRideRequest);

        assertNotNull(result);
        verify(rideRepository, times(1)).save(any());
        verify(stopService, times(1)).createStops(any(), any());
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
        List<StopResponse> stopResponses = TestRideUtil.getRideStopResponses();
        List<StopRequest> stopRequests = TestRideUtil.getRideStopRequests();
        PassengerRideResponse passengerRideResponse = TestRideUtil.getPassengerRideResponse();
        DriverResponse driverResponse = TestRideUtil.getDriverResponse();

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));
        when(rideRepository.save(existingRide)).thenReturn(existingRide);
        when(driverWebClient.getDriver(anyLong())).thenReturn(driverResponse);
        when(stopService.editStops(stopRequests, existingRide)).thenReturn(stopResponses);
        when(rideMapper.mapRideToPassengerRideResponse(existingRide, stopResponses, driverResponse, driverResponse.getCar()))
                .thenReturn(passengerRideResponse);

        PassengerRideResponse result = rideService.editRide(rideId, editRideRequest);

        assertNotNull(result);
        verify(rideRepository, times(1)).save(existingRide);
        verify(stopService, times(1)).editStops(editRideRequest.getStops(), existingRide);
    }

    @Test
    void testEditRide_WhenStatusIsCompleted_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        EditRideRequest editRideRequest = TestRideUtil.getEditRideRequest();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.COMPLETED);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.editRide(rideId, editRideRequest));

        verify(rideRepository, never()).save(any());
        verify(stopService, never()).editStops(anyList(), any());
    }

    @Test
    void testEditRide_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();
        EditRideRequest editRideRequest = TestRideUtil.getEditRideRequest();

        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.editRide(rideId, editRideRequest));
    }

    @Test
    void testGetRideByRideId_WhenRideExists_ShouldReturnRideResponse() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        RideResponse expectedResponse = TestRideUtil.getFirstRideResponse();
        List<StopResponse> stops = TestRideUtil.getRideStopResponses();

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));
        when(stopService.getRideStops(existingRide)).thenReturn(stops);
        when(rideMapper.mapRideToRideResponse(existingRide, stops)).thenReturn(expectedResponse);

        RideResponse result = rideService.getRideByRideId(rideId);

        assertEquals(expectedResponse, result);
    }

    @Test
    void testGetRideByRideId_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();

        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.getRideByRideId(rideId));
    }

    @Test
    void testCancelRide_WhenRideStatusIsValid_ShouldCancelRide() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.CREATED);
        RideResponse expectedResponse = TestRideUtil.getFirstRideResponse();
        List<StopResponse> stops = TestRideUtil.getRideStopResponses();

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));
        when(stopService.getRideStops(existingRide)).thenReturn(stops);
        when(rideRepository.save(existingRide)).thenReturn(existingRide);
        when(rideMapper.mapRideToRideResponse(existingRide, stops)).thenReturn(expectedResponse);

        RideResponse result = rideService.cancelRide(rideId);

        assertEquals(expectedResponse, result);
        assertEquals(RideStatus.CANCELED, existingRide.getStatus());
        verify(rideRepository, times(1)).save(existingRide);
    }

    @Test
    void testCancelRide_WhenRideStatusIsNotValid_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.COMPLETED);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.cancelRide(rideId));
        assertEquals(RideStatus.COMPLETED, existingRide.getStatus());
    }

    @Test
    void testCancelRide_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();

        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.cancelRide(rideId));
    }

    @Test
    void testStartRide_WhenRideStatusIsValid_ShouldStartRide() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.CREATED);
        RideResponse expectedResponse = TestRideUtil.getFirstRideResponse();
        List<StopResponse> stops = TestRideUtil.getRideStopResponses();

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));
        when(stopService.getRideStops(existingRide)).thenReturn(stops);
        when(rideRepository.save(existingRide)).thenReturn(existingRide);
        when(rideMapper.mapRideToRideResponse(existingRide, stops)).thenReturn(expectedResponse);

        RideResponse result = rideService.startRide(rideId);

        assertEquals(expectedResponse, result);
        assertEquals(RideStatus.STARTED, existingRide.getStatus());
        assertNotNull(existingRide.getStartDateTime());
        verify(rideRepository, times(1)).save(existingRide);
    }

    @Test
    void testStartRide_WhenRideStatusIsNotValid_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.COMPLETED);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.startRide(rideId));
        assertEquals(RideStatus.COMPLETED, existingRide.getStatus());
    }

    @Test
    void testStartRide_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();

        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.startRide(rideId));
    }

    @Test
    void testCompleteRide_WhenRideStatusIsValid_ShouldCompleteRide() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.STARTED);
        RideResponse expectedResponse = TestRideUtil.getFirstRideResponse();
        List<StopResponse> stops = TestRideUtil.getRideStopResponses();

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));
        when(stopService.getRideStops(existingRide)).thenReturn(stops);
        when(rideRepository.save(existingRide)).thenReturn(existingRide);
        when(rideMapper.mapRideToRideResponse(existingRide, stops)).thenReturn(expectedResponse);

        RideResponse result = rideService.completeRide(rideId);

        assertEquals(expectedResponse, result);
        assertEquals(RideStatus.COMPLETED, existingRide.getStatus());
        assertNotNull(existingRide.getEndDateTime());
        verify(rideRepository, times(1)).save(existingRide);
        verify(driverWebClient, times(1)).changeDriverStatusToFree(anyLong());
        verify(bankWebClient, times(1)).refillDriverBankAccount(any());
    }

    @Test
    void testCompleteRide_WhenRideStatusIsNotValid_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.CANCELED);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.completeRide(rideId));
        assertEquals(RideStatus.CANCELED, existingRide.getStatus());
    }

    @Test
    void testCompleteRide_WhenRideIsNotStarted_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        existingRide.setStatus(RideStatus.CREATED);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.completeRide(rideId));
        assertEquals(RideStatus.CREATED, existingRide.getStatus());
    }

    @Test
    void testCompleteRide_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();

        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.completeRide(rideId));
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

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));

        rideService.ratePassenger(rideId, ratingRequest);

        verify(kafkaSendRatingGateway, times(1)).sendPassengerRating(expectedRatingMessage);
    }

    @Test
    void testRatePassenger_WhenRideStatusIsNotCompleted_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();
        existingRide.setStatus(RideStatus.STARTED);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.ratePassenger(rideId, ratingRequest));
        assertEquals(RideStatus.STARTED, existingRide.getStatus());
    }

    @Test
    void testRatePassenger_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();

        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.ratePassenger(rideId, ratingRequest));
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

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));

        rideService.rateDriver(rideId, ratingRequest);

        verify(kafkaSendRatingGateway, times(1)).sendDriverRating(expectedRatingMessage);
    }

    @Test
    void testRateDriver_WhenRideStatusIsNotCompleted_ShouldThrowRideStatusException() {
        Long rideId = TestRideUtil.getFirstRideId();
        Ride existingRide = TestRideUtil.getFirstRide();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();
        existingRide.setStatus(RideStatus.STARTED);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(existingRide));

        assertThrows(RideStatusException.class, () -> rideService.rateDriver(rideId, ratingRequest));
        assertEquals(RideStatus.STARTED, existingRide.getStatus());
    }

    @Test
    void testRateDriver_WhenRideDoesNotExist_ShouldThrowRideNotFoundException() {
        Long rideId = TestRideUtil.getFirstRideId();
        RatingRequest ratingRequest = TestRideUtil.getRatingRequest();

        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.rateDriver(rideId, ratingRequest));
    }
}