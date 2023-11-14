package com.example.passengerservice.service.impl;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;
import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.exception.PassengerRatingNotFoundException;
import com.example.passengerservice.model.PassengerRating;
import com.example.passengerservice.repository.PassengerRatingRepository;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerRatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PassengerRatingServiceImpl implements PassengerRatingService {
    private final PassengerRatingRepository passengerRatingRepository;
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private static final String PASSENGER_RATINGS_NOT_FOUND = "Passenger ratings not found!";
    private static final String PASSENGER_NOT_FOUND = "Passenger not found!";

    @Override
    public PassengerRatingResponse ratePassenger(PassengerRatingRequest passengerRatingRequest) {
        validatePassengerExists(passengerRatingRequest.getPassengerId());
        PassengerRating newPassengerRating = mapPassengerRatingRequestToPassengerRating(passengerRatingRequest);
        newPassengerRating = passengerRatingRepository.save(newPassengerRating);
        return mapPassengerRatingToPassengerRatingResponse(newPassengerRating);
    }

    @Override
    public List<PassengerRatingResponse> getRatingsByPassengerId(long id) {
        validatePassengerExists(id);
        List<PassengerRating> passengerRatings = passengerRatingRepository.getPassengerRatingsByPassengerId(id);
        if (passengerRatings.isEmpty())
            throw new PassengerRatingNotFoundException(PASSENGER_RATINGS_NOT_FOUND);
        else
            return passengerRatings.stream()
                    .map(this::mapPassengerRatingToPassengerRatingResponse)
                    .collect(Collectors.toList());
    }

    @Override
    public AveragePassengerRatingResponse getAveragePassengerRating(long id) {
        validatePassengerExists(id);
        List<PassengerRating> passengerRatings = passengerRatingRepository.getPassengerRatingsByPassengerId(id);
        if (passengerRatings.isEmpty())
            throw new PassengerRatingNotFoundException(PASSENGER_RATINGS_NOT_FOUND);
        else {
            double averageRating = passengerRatings.stream()
                    .mapToDouble(PassengerRating::getRating)
                    .average()
                    .orElse(0.0);
            return AveragePassengerRatingResponse.builder()
                    .averageRating(Math.round(averageRating * 100.0) / 100.0)
                    .passengerId(id)
                    .build();
        }
    }


    public void validatePassengerExists(long passengerId) {
        if (!passengerRepository.existsById(passengerId)) {
            throw new PassengerNotFoundException(PASSENGER_NOT_FOUND);
        }
    }

    public PassengerRating mapPassengerRatingRequestToPassengerRating(PassengerRatingRequest passengerRatingRequest) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(passengerRatingRequest, PassengerRating.class);
    }

    private PassengerRatingResponse mapPassengerRatingToPassengerRatingResponse(PassengerRating passengerRating) {
        return modelMapper.map(passengerRating, PassengerRatingResponse.class);
    }
}
