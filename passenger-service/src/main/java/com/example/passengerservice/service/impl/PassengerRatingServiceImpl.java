package com.example.passengerservice.service.impl;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.dto.response.AllPassengerRatingsResponse;
import com.example.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.example.passengerservice.dto.response.PassengerRatingResponse;
import com.example.passengerservice.exception.PassengerNotFoundException;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.PassengerRating;
import com.example.passengerservice.repository.PassengerRatingRepository;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.PassengerRatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerRatingServiceImpl implements PassengerRatingService {
    private final PassengerRatingRepository passengerRatingRepository;
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private static final String PASSENGER_NOT_FOUND = "Passenger with id '%s' not found";

    @Override
    public Mono<Void> ratePassenger(PassengerRatingRequest passengerRatingRequest) {
        String passengerId = passengerRatingRequest.getPassengerId();
        log.info("Rating passenger with id {}: {}", passengerId, passengerRatingRequest);

        return getPassenger(passengerId)
                .flatMap(passenger -> {
                    PassengerRating newPassengerRating = PassengerRating.builder()
                            .driverId(passengerRatingRequest.getDriverId())
                            .rating(passengerRatingRequest.getRating())
                            .rideId(passengerRatingRequest.getRideId())
                            .passenger(passenger)
                            .build();
                    return passengerRatingRepository.save(newPassengerRating);
                })
                .then();
    }

    @Override
    public Mono<AllPassengerRatingsResponse> getRatingsByPassengerId(String passengerId) {
        log.info("Retrieving ratings for passenger with id: {}", passengerId);

        return getPassenger(passengerId)
                .flatMapMany(passenger -> passengerRatingRepository.getPassengerRatingsByPassengerId(passengerId))
                .map(passengerRating -> mapPassengerRatingToPassengerRatingResponse(passengerRating, passengerId))
                .collectList()
                .map(passengerRatingResponses -> AllPassengerRatingsResponse.builder()
                        .passengerRatings(passengerRatingResponses)
                        .build());
    }

    @Override
    public Mono<AveragePassengerRatingResponse> getAveragePassengerRating(String passengerId) {
        log.info("Retrieving average rating for passenger with id: {}", passengerId);

        return getPassenger(passengerId)
                .flatMapMany(passenger -> passengerRatingRepository.getPassengerRatingsByPassengerId(passengerId))
                .collectList()
                .map(passengerRatings -> {
                    double averageRating = passengerRatings.stream()
                            .mapToDouble(PassengerRating::getRating)
                            .average()
                            .orElse(0.0);
                    return AveragePassengerRatingResponse.builder()
                            .averageRating(Math.round(averageRating * 100.0) / 100.0)
                            .passengerId(passengerId)
                            .build();
                });
    }


    private Mono<Passenger> getPassenger(String passengerId) {
        return passengerRepository.findById(passengerId)
                .filter(Passenger::isActive)
                .switchIfEmpty(Mono.error(() -> {
                    log.error("Passenger with id {} not found", passengerId);
                    return new PassengerNotFoundException(String.format(PASSENGER_NOT_FOUND, passengerId));
                }));
    }

    private PassengerRatingResponse mapPassengerRatingToPassengerRatingResponse(PassengerRating passengerRating, String passengerId) {
        PassengerRatingResponse passengerRatingResponse = modelMapper.map(passengerRating, PassengerRatingResponse.class);
        passengerRatingResponse.setPassengerId(passengerId);
        return passengerRatingResponse;
    }
}
