package com.example.driverservice.kafka.service.impl;

import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.kafka.service.KafkaFreeDriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaFreeDriverServiceImpl implements KafkaFreeDriverService {
    private final KafkaTemplate<String, DriverResponse> kafkaTemplate;
    @Value("${topic.name.free-driver}")
    private String topicName;

    @Override
    public void sendFreeDriverToConsumer(DriverResponse driver) {
        log.info("Sending free driver to Kafka topic: {}", topicName);

        kafkaTemplate.send(topicName, driver);
    }
}