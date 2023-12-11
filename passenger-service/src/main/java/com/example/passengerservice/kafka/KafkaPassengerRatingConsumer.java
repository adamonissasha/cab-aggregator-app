package com.example.passengerservice.kafka;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.service.PassengerRatingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class KafkaPassengerRatingConsumer {

    @Bean
    public Consumer<PassengerRatingRequest> ratePassenger(PassengerRatingService passengerRatingService) {
        return passengerRatingService::ratePassenger;
    }
}
