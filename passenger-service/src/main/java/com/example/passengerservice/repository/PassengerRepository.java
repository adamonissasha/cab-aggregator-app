package com.example.passengerservice.repository;

import com.example.passengerservice.model.Passenger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PassengerRepository extends ReactiveMongoRepository<Passenger, String> {
    Mono<Passenger> findPassengerByPhoneNumber(String phoneNumber);

    Flux<Passenger> findAllByIsActiveTrue(Pageable pageable);

    Mono<Long> countAllByIsActiveTrue();

}
