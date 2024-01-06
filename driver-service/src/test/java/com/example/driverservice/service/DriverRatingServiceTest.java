package com.example.driverservice.service;

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
public class DriverRatingServiceTest {
    @Mock
    DriverRatingRepository driverRatingRepository;
    @Mock
    DriverRepository driverRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    DriverRatingServiceImpl driverRatingService;

    @Test
    public void testRateDriver_WhenDriverExists_ShouldRateDriver() {
        DriverRatingRequest driverRatingRequest = TestDriverRatingUtil.getDriverRatingRequest();
        Driver existingDriver = TestDriverUtil.getFirstDriver();

        when(driverRepository.findById(driverRatingRequest.getDriverId()))
                .thenReturn(Optional.of(existingDriver));
        when(driverRatingRepository.save(any(DriverRating.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> driverRatingService.rateDriver(driverRatingRequest));

        verify(driverRatingRepository, times(1))
                .save(any(DriverRating.class));
        verify(driverRepository, times(1))
                .findById(driverRatingRequest.getDriverId());
    }

    @Test
    public void testRateDriver_WhenDriverNotFound_ShouldThrowDriverNotFoundException() {
        DriverRatingRequest driverRatingRequest = TestDriverRatingUtil.getDriverRatingRequest();

        when(driverRepository.findById(driverRatingRequest.getDriverId()))
                .thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverRatingService.rateDriver(driverRatingRequest));

        verify(driverRatingRepository, never())
                .save(any(DriverRating.class));
        verify(driverRepository, times(1))
                .findById(driverRatingRequest.getDriverId());
    }

    @Test
    public void testGetRatingsByDriverId_WhenDriverExists_ShouldReturnDriverRatings() {
        Long driverId = TestDriverUtil.getFirstDriverId();
        Driver driver = TestDriverUtil.getFirstDriver();

        DriverRating firstDriverRating = TestDriverRatingUtil.getFirstDriverRating();
        DriverRating secondDriverRating = TestDriverRatingUtil.getSecondDriverRating();
        List<DriverRating> driverRatings = Arrays.asList(firstDriverRating, secondDriverRating);

        DriverRatingResponse firstDriverRatingResponse = TestDriverRatingUtil.getFirstDriverRatingResponse();
        DriverRatingResponse secondDriverRatingResponse = TestDriverRatingUtil.getSecondDriverRatingResponse();
        List<DriverRatingResponse> expectedDriverRatings = Arrays.asList(firstDriverRatingResponse, secondDriverRatingResponse);

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.of(driver));
        when(driverRatingRepository.getDriverRatingsByDriverId(driverId))
                .thenReturn(driverRatings);
        when(modelMapper.map(firstDriverRating, DriverRatingResponse.class))
                .thenReturn(firstDriverRatingResponse);
        when(modelMapper.map(secondDriverRating, DriverRatingResponse.class))
                .thenReturn(secondDriverRatingResponse);

        AllDriverRatingsResponse result = driverRatingService.getRatingsByDriverId(driverId);

        assertDoesNotThrow(() -> driverRatingService.validateDriverExists(driverId));
        assertEquals(expectedDriverRatings.size(), result.getDriverRatings().size());

        verify(driverRepository, times(2))
                .findById(driverId);
        verify(driverRatingRepository, times(1))
                .getDriverRatingsByDriverId(driverId);
    }

    @Test
    void testGetRatingsByDriverId_WhenDriverNotExists_ShouldThrowDriverNotFoundException() {
        Long driverId = TestDriverUtil.getFirstDriverId();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverRatingService.getRatingsByDriverId(driverId));

        verify(driverRepository, times(1))
                .findById(driverId);
    }

    @Test
    void testGetAverageDriverRating_WhenDriverExists_ShouldReturnAverageDriverRatingResponse() {
        Long driverId = TestDriverUtil.getFirstDriverId();
        Driver driver = TestDriverUtil.getFirstDriver();
        DriverRating firstDriverRating = TestDriverRatingUtil.getFirstDriverRating();
        DriverRating secondDriverRating = TestDriverRatingUtil.getSecondDriverRating();
        List<DriverRating> driverRatings = Arrays.asList(firstDriverRating, secondDriverRating);
        Double expectedAverageRating = TestDriverRatingUtil.getAverageDriverRating();
        AverageDriverRatingResponse expected = AverageDriverRatingResponse.builder()
                .averageRating(expectedAverageRating)
                .passengerId(firstDriverRating.getPassengerId())
                .build();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.of(driver));
        when(driverRatingRepository.getDriverRatingsByDriverId(driverId))
                .thenReturn(driverRatings);

        AverageDriverRatingResponse actual = driverRatingService.getAverageDriverRating(driverId);

        assertEquals(expected, actual);

        verify(driverRepository, times(1))
                .findById(driverId);
        verify(driverRatingRepository, times(1))
                .getDriverRatingsByDriverId(driverId);
    }

    @Test
    void testGetAverageDriverRating_WhenDriverNotExists_ShouldThrowDriverNotFoundException() {
        Long driverId = TestDriverUtil.getFirstDriverId();

        when(driverRepository.findById(driverId))
                .thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverRatingService.getAverageDriverRating(driverId));

        verify(driverRepository, times(1))
                .findById(driverId);
    }
}