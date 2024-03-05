package com.example.passengerservice.sql;

import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.PassengerRating;
import com.example.passengerservice.util.TestPassengerRatingUtil;
import com.example.passengerservice.util.TestPassengerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitDb {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public void addTestData() {
        Passenger passenger1 = TestPassengerUtil.getFirstPassenger();
        Passenger passenger2 = TestPassengerUtil.getSecondPassenger();
        reactiveMongoTemplate.save(passenger1)
                .block();
        reactiveMongoTemplate.save(passenger2)
                .block();

        PassengerRating rating1 = TestPassengerRatingUtil.getFirstPassengerRating();
        PassengerRating rating2 = TestPassengerRatingUtil.getSecondPassengerRating();
        reactiveMongoTemplate.save(rating1)
                .block();
        reactiveMongoTemplate.save(rating2)
                .block();
    }

    public void deleteTestData() {
        reactiveMongoTemplate.remove(Passenger.class)
                .all()
                .block();
        reactiveMongoTemplate.remove(PassengerRating.class)
                .all()
                .block();
    }
}