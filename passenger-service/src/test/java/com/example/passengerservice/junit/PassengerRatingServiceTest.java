package com.example.passengerservice.junit;

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
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassengerRatingServiceTest {
    @Mock
    PassengerRatingRepository passengerRatingRepository;
    @Mock
    PassengerRepository passengerRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    PassengerRatingServiceImpl passengerRatingService;

    @Test
    public void testRatePassenger_WhenPassengerExists_ShouldRatePassenger() {
        PassengerRatingRequest passengerRatingRequest = TestPassengerRatingUtil.getPassengerRatingRequest();
        Passenger existingPassenger = TestPassengerUtil.getFirstPassenger();

        when(passengerRepository.findById(passengerRatingRequest.getPassengerId()))
                .thenReturn(Mono.just(existingPassenger));
        when(passengerRatingRepository.save(any(PassengerRating.class)))
                .thenReturn(Mono.just(TestPassengerRatingUtil.getFirstPassengerRating()));

        StepVerifier.create(passengerRatingService.ratePassenger(passengerRatingRequest))
                .verifyComplete();

        verify(passengerRatingRepository, times(1))
                .save(any(PassengerRating.class));
        verify(passengerRepository, times(1))
                .findById(passengerRatingRequest.getPassengerId());
    }

    @Test
    public void testRatePassenger_WhenPassengerNotFound_ShouldThrowPassengerNotFoundException() {
        PassengerRatingRequest passengerRatingRequest = TestPassengerRatingUtil.getPassengerRatingRequest();

        when(passengerRepository.findById(passengerRatingRequest.getPassengerId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(passengerRatingService.ratePassenger(passengerRatingRequest))
                .expectError(PassengerNotFoundException.class)
                .verify();

        verify(passengerRatingRepository, never())
                .save(any(PassengerRating.class));
        verify(passengerRepository, times(1))
                .findById(passengerRatingRequest.getPassengerId());
    }

    @Test
    public void testGetRatingsByPassengerId_WhenPassengerExists_ShouldReturnPassengerRatings() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();

        PassengerRating firstPassengerRating = TestPassengerRatingUtil.getFirstPassengerRating();
        PassengerRating secondPassengerRating = TestPassengerRatingUtil.getSecondPassengerRating();
        List<PassengerRating> passengerRatings = Arrays.asList(firstPassengerRating, secondPassengerRating);

        PassengerRatingResponse firstPassengerRatingResponse = TestPassengerRatingUtil.getFirstPassengerRatingResponse();
        PassengerRatingResponse secondPassengerRatingResponse = TestPassengerRatingUtil.getSecondPassengerRatingResponse();
        List<PassengerRatingResponse> expectedPassengerRatings = Arrays.asList(firstPassengerRatingResponse, secondPassengerRatingResponse);
        AllPassengerRatingsResponse expected = AllPassengerRatingsResponse.builder()
                .passengerRatings(expectedPassengerRatings)
                .build();

        when(passengerRatingRepository.getPassengerRatingsByPassengerId(passengerId))
                .thenReturn(Flux.fromIterable(passengerRatings));
        when(modelMapper.map(firstPassengerRating, PassengerRatingResponse.class))
                .thenReturn(firstPassengerRatingResponse);
        when(modelMapper.map(secondPassengerRating, PassengerRatingResponse.class))
                .thenReturn(secondPassengerRatingResponse);
        when(passengerRepository.findById(passengerId))
                .thenReturn(Mono.just(TestPassengerUtil.getFirstPassenger()));

        StepVerifier.create(passengerRatingService.getRatingsByPassengerId(passengerId))
                .expectNext(expected)
                .verifyComplete();


        verify(passengerRatingRepository, times(1))
                .getPassengerRatingsByPassengerId(passengerId);
        verify(passengerRepository, times(1))
                .findById(passengerId);
    }

    @Test
    void testGetRatingsByPassengerId_WhenPassengerNotExists_ShouldThrowPassengerNotFoundException() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();

        when(passengerRepository.findById(passengerId))
                .thenReturn(Mono.empty());

        StepVerifier.create(passengerRatingService.getRatingsByPassengerId(passengerId))
                .expectError(PassengerNotFoundException.class)
                .verify();

        verify(passengerRepository, times(1))
                .findById(passengerId);
    }

    @Test
    void testGetAveragePassengerRating_WhenPassengerExists_ShouldReturnAveragePassengerRatingResponse() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRating firstPassengerRating = TestPassengerRatingUtil.getFirstPassengerRating();
        PassengerRating secondPassengerRating = TestPassengerRatingUtil.getSecondPassengerRating();
        List<PassengerRating> passengerRatings = Arrays.asList(firstPassengerRating, secondPassengerRating);
        Double expectedAverageRating = TestPassengerRatingUtil.getAveragePassengerRating();
        AveragePassengerRatingResponse expected =
                AveragePassengerRatingResponse.builder()
                        .averageRating(expectedAverageRating)
                        .passengerId(passengerId)
                        .build();

        when(passengerRepository.findById(passengerId))
                .thenReturn(Mono.just(TestPassengerUtil.getFirstPassenger()));
        when(passengerRatingRepository.getPassengerRatingsByPassengerId(passengerId))
                .thenReturn(Flux.fromIterable(passengerRatings));

        StepVerifier.create(passengerRatingService.getAveragePassengerRating(passengerId))
                .expectNext(expected)
                .verifyComplete();

        verify(passengerRepository, times(1))
                .findById(passengerId);
        verify(passengerRatingRepository, times(1))
                .getPassengerRatingsByPassengerId(passengerId);
    }

    @Test
    void testGetAveragePassengerRating_WhenPassengerNotExists_ShouldThrowPassengerNotFoundException() {
        String passengerId = TestPassengerUtil.getFirstPassengerId();

        when(passengerRepository.findById(passengerId))
                .thenReturn(Mono.empty());

        StepVerifier.create(passengerRatingService.getAveragePassengerRating(passengerId))
                .expectError(PassengerNotFoundException.class)
                .verify();

        verify(passengerRepository, times(1))
                .findById(passengerId);
    }
}