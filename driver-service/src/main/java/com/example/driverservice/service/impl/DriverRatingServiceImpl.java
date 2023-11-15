package com.example.driverservice.service.impl;

import com.example.driverservice.dto.request.DriverRatingRequest;
import com.example.driverservice.dto.response.AllDriverRatingsResponse;
import com.example.driverservice.dto.response.AverageDriverRatingResponse;
import com.example.driverservice.dto.response.DriverRatingResponse;
import com.example.driverservice.exception.DriverNotFoundException;
import com.example.driverservice.exception.DriverRatingNotFoundException;
import com.example.driverservice.model.DriverRating;
import com.example.driverservice.repository.DriverRatingRepository;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.DriverRatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverRatingServiceImpl implements DriverRatingService {
    private final DriverRatingRepository driverRatingRepository;
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;
    private static final String DRIVER_NOT_FOUND = "Driver not found!";
    private static final String NO_DRIVER_RATING = "There is no rating of this driver!";


    @Override
    public DriverRatingResponse rateDriver(DriverRatingRequest driverRatingRequest, long driverId) {
        DriverRating newDriverRating = mapDriverRatingRequestToDriverRating(driverRatingRequest);
        newDriverRating.setDriver(driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(DRIVER_NOT_FOUND)));
        newDriverRating = driverRatingRepository.save(newDriverRating);
        return mapDriverRatingToDriverRatingResponse(newDriverRating);
    }

    @Override
    public AllDriverRatingsResponse getRatingsByDriverId(long driverId) {
        validateDriverExists(driverId);
        List<DriverRatingResponse> driverRatings = driverRatingRepository.getDriverRatingsByDriverId(driverId)
                .stream()
                .map(this::mapDriverRatingToDriverRatingResponse)
                .toList();
        return AllDriverRatingsResponse.builder()
                .driverRatings(driverRatings)
                .build();
    }


    @Override
    public AverageDriverRatingResponse getAverageDriverRating(long driverId) {
        validateDriverExists(driverId);
        List<DriverRating> driverRatings = driverRatingRepository.getDriverRatingsByDriverId(driverId);
        if (driverRatings.isEmpty())
            throw new DriverRatingNotFoundException(NO_DRIVER_RATING);
        double averageRating = driverRatings.stream()
                .mapToDouble(DriverRating::getRating)
                .average()
                .orElse(0.0);
        return AverageDriverRatingResponse.builder()
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .passengerId(driverId)
                .build();
    }

    public void validateDriverExists(long driverId) {
        driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(DRIVER_NOT_FOUND));
    }

    public DriverRating mapDriverRatingRequestToDriverRating(DriverRatingRequest driverRatingRequest) {
        return modelMapper.map(driverRatingRequest, DriverRating.class);
    }

    private DriverRatingResponse mapDriverRatingToDriverRatingResponse(DriverRating driverRating) {
        return modelMapper.map(driverRating, DriverRatingResponse.class);
    }
}
