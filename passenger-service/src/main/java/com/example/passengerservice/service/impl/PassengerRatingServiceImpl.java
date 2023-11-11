package com.example.passengerservice.service.impl;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;
import com.example.passengerservice.exception.PassengerRatingNotFoundException;
import com.example.passengerservice.model.PassengerRating;
import com.example.passengerservice.repository.PassengerRatingRepository;
import com.example.passengerservice.service.PassengerRatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PassengerRatingServiceImpl implements PassengerRatingService {
    private final PassengerRatingRepository passengerRatingRepository;
    private final ModelMapper modelMapper;
    private static final String PASSENGER_RATINGS_NOT_FOUND = "Passenger ratings not found!";

    @Override
    public PassengerRatingResponse ratePassenger(PassengerRatingRequest passengerRatingRequest) {
        PassengerRating newPassengerRating = mapPassengerRatingRequestToPassengerRating(passengerRatingRequest);
        newPassengerRating = passengerRatingRepository.save(newPassengerRating);
        return mapPassengerRatingToPassengerRatingResponse(newPassengerRating);
    }

    @Override
    public List<PassengerRatingResponse> getRatingsByPassengerId(long id) {
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
        List<PassengerRating> passengerRatings = passengerRatingRepository.getPassengerRatingsByPassengerId(id);
        double averageRating = passengerRatings.stream()
                .mapToDouble(PassengerRating::getRating)
                .average()
                .orElse(0.0);
        return AveragePassengerRatingResponse.builder()
                .averageRating(Math.round(averageRating * 10.0) / 10.0)
                .passengerId(id)
                .build();
    }


    public PassengerRating mapPassengerRatingRequestToPassengerRating(PassengerRatingRequest passengerRatingRequest) {
        modelMapper.addMappings(new PropertyMap<PassengerRatingRequest, PassengerRating>() {
            @Override
            protected void configure() {
                skip().setId(null);
            }
        });
        return modelMapper.map(passengerRatingRequest, PassengerRating.class);
    }

    private PassengerRatingResponse mapPassengerRatingToPassengerRatingResponse(PassengerRating passengerRating) {
        return modelMapper.map(passengerRating, PassengerRatingResponse.class);
    }
}
