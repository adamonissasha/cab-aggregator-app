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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
                .thenReturn(Optional.of(existingPassenger));
        when(passengerRatingRepository.save(any(PassengerRating.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> passengerRatingService.ratePassenger(passengerRatingRequest));

        verify(passengerRatingRepository, times(1))
                .save(any(PassengerRating.class));
        verify(passengerRepository, times(1))
                .findById(passengerRatingRequest.getPassengerId());
    }

    @Test
    public void testRatePassenger_WhenPassengerNotFound_ShouldThrowPassengerNotFoundException() {
        PassengerRatingRequest passengerRatingRequest = TestPassengerRatingUtil.getPassengerRatingRequest();

        when(passengerRepository.findById(passengerRatingRequest.getPassengerId()))
                .thenReturn(Optional.empty());

        assertThrows(PassengerNotFoundException.class, () -> passengerRatingService.ratePassenger(passengerRatingRequest));

        verify(passengerRatingRepository, never())
                .save(any(PassengerRating.class));
        verify(passengerRepository, times(1))
                .findById(passengerRatingRequest.getPassengerId());
    }

    @Test
    public void testGetRatingsByPassengerId_WhenPassengerExists_ShouldReturnPassengerRatings() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();

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
                .thenReturn(passengerRatings);
        when(modelMapper.map(firstPassengerRating, PassengerRatingResponse.class))
                .thenReturn(firstPassengerRatingResponse);
        when(modelMapper.map(secondPassengerRating, PassengerRatingResponse.class))
                .thenReturn(secondPassengerRatingResponse);
        when(passengerRepository.existsById(passengerId))
                .thenReturn(true);

        AllPassengerRatingsResponse actual = passengerRatingService.getRatingsByPassengerId(passengerId);

        assertDoesNotThrow(() -> passengerRatingService.validatePassengerExists(passengerId));
        assertEquals(expected, actual);

        verify(passengerRatingRepository, times(1))
                .getPassengerRatingsByPassengerId(passengerId);
        verify(passengerRepository, times(2))
                .existsById(passengerId);
    }

    @Test
    void testGetRatingsByPassengerId_WhenPassengerNotExists_ShouldThrowPassengerNotFoundException() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();

        when(passengerRepository.existsById(passengerId))
                .thenReturn(false);

        assertThrows(PassengerNotFoundException.class, () -> passengerRatingService.getRatingsByPassengerId(passengerId));

        verify(passengerRepository, times(1))
                .existsById(passengerId);
    }

    @Test
    void testGetAveragePassengerRating_WhenPassengerExists_ShouldReturnAveragePassengerRatingResponse() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();
        PassengerRating firstPassengerRating = TestPassengerRatingUtil.getFirstPassengerRating();
        PassengerRating secondPassengerRating = TestPassengerRatingUtil.getSecondPassengerRating();
        List<PassengerRating> passengerRatings = Arrays.asList(firstPassengerRating, secondPassengerRating);
        Double expectedAverageRating = TestPassengerRatingUtil.getAveragePassengerRating();
        AveragePassengerRatingResponse expected =
                AveragePassengerRatingResponse.builder()
                        .averageRating(expectedAverageRating)
                        .passengerId(passengerId)
                        .build();

        when(passengerRepository.existsById(passengerId))
                .thenReturn(true);
        when(passengerRatingRepository.getPassengerRatingsByPassengerId(passengerId))
                .thenReturn(passengerRatings);

        AveragePassengerRatingResponse actual = passengerRatingService.getAveragePassengerRating(passengerId);

        assertEquals(expected, actual);

        verify(passengerRepository, times(1))
                .existsById(passengerId);
        verify(passengerRatingRepository, times(1))
                .getPassengerRatingsByPassengerId(passengerId);
    }

    @Test
    void testGetAveragePassengerRating_WhenPassengerNotExists_ShouldThrowPassengerNotFoundException() {
        Long passengerId = TestPassengerUtil.getFirstPassengerId();

        when(passengerRepository.existsById(passengerId))
                .thenReturn(false);

        assertThrows(PassengerNotFoundException.class, () -> passengerRatingService.getAveragePassengerRating(passengerId));

        verify(passengerRepository, times(1))
                .existsById(passengerId);
    }
}