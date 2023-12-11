package com.example.driverservice.kafka.service;

import com.example.driverservice.dto.request.DriverRatingRequest;
import com.example.driverservice.service.DriverRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaDriverRatingConsumerService {
    private final DriverRatingService driverRatingService;

    @KafkaListener(topics = "${topic.name.driver-rating}", groupId = "${topic.group-id.driver-rating}")
    public void consumeDriverRating(DriverRatingRequest driverRatingRequest) {
        driverRatingService.rateDriver(driverRatingRequest);
    }
}