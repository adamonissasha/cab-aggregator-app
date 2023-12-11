package com.example.passengerservice.service.impl;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;
import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.model.PassengerRating;
import com.example.passengerservice.repository.PassengerRatingRepository;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerRatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerRatingServiceImpl implements PassengerRatingService {
    private final PassengerRatingRepository passengerRatingRepository;
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private static final String PASSENGER_NOT_FOUND = "Passenger with id '%s' not found";

    @Override
    public void ratePassenger(PassengerRatingRequest passengerRatingRequest) {
        Long passengerId = passengerRatingRequest.getPassengerId();
        passengerRepository.findById(passengerId)
                .ifPresent(passenger -> {
                    PassengerRating newPassengerRating = PassengerRating.builder()
                            .driverId(passengerRatingRequest.getDriverId())
                            .rating(passengerRatingRequest.getRating())
                            .rideId(passengerRatingRequest.getRideId())
                            .passenger(passenger)
                            .build();
                    passengerRatingRepository.save(newPassengerRating);
                });
    }

    @Override
    public AllPassengerRatingsResponse getRatingsByPassengerId(long passengerId) {
        validatePassengerExists(passengerId);
        List<PassengerRatingResponse> passengerRatings =
                passengerRatingRepository.getPassengerRatingsByPassengerId(passengerId)
                        .stream()
                        .map(this::mapPassengerRatingToPassengerRatingResponse)
                        .toList();
        return AllPassengerRatingsResponse.builder()
                .passengerRatings(passengerRatings)
                .build();
    }

    @Override
    public AveragePassengerRatingResponse getAveragePassengerRating(long passengerId) {
        validatePassengerExists(passengerId);
        List<PassengerRating> passengerRatings = passengerRatingRepository.getPassengerRatingsByPassengerId(passengerId);
        double averageRating = passengerRatings.stream()
                .mapToDouble(PassengerRating::getRating)
                .average()
                .orElse(0.0);
        return AveragePassengerRatingResponse.builder()
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .passengerId(passengerId)
                .build();
    }


    public void validatePassengerExists(long passengerId) {
        if (!passengerRepository.existsById(passengerId)) {
            throw new PassengerNotFoundException(String.format(PASSENGER_NOT_FOUND, passengerId));
        }
    }

    private PassengerRatingResponse mapPassengerRatingToPassengerRatingResponse(PassengerRating passengerRating) {
        return modelMapper.map(passengerRating, PassengerRatingResponse.class);
    }
}
