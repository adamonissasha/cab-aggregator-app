package com.example.passengerservice.repository;

import com.example.passengerservice.model.PassengerRating;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PassengerRatingRepository extends ReactiveMongoRepository<PassengerRating, String> {
    Flux<PassengerRating> getPassengerRatingsByPassengerId(String passengerId);
}
