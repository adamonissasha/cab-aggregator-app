package com.example.driverservice.kafka.service;

import com.example.driverservice.dto.request.DriverRatingRequest;
import com.example.driverservice.service.DriverRatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaDriverRatingConsumerService {
    private final DriverRatingService driverRatingService;

    @KafkaListener(topics = "${topic.name.driver-rating}", groupId = "${topic.group-id.driver-rating}")
    public void consumeDriverRating(DriverRatingRequest driverRatingRequest) {
        log.info("Received driver rating request: {}", driverRatingRequest);

        driverRatingService.rateDriver(driverRatingRequest);
    }
}