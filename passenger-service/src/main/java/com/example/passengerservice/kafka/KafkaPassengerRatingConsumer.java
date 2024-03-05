package com.example.passengerservice.kafka;

import com.example.passengerservice.dto.request.PassengerRatingRequest;
import com.example.passengerservice.service.PassengerRatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class KafkaPassengerRatingConsumer {

    @Bean
    public Consumer<PassengerRatingRequest> ratePassenger(PassengerRatingService passengerRatingService) {
        return passengerRatingRequest -> {
            log.info("Received passenger rating request: {}", passengerRatingRequest);

            passengerRatingService.ratePassenger(passengerRatingRequest);
        };
    }
}
