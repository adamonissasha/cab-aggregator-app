package com.example.driverservice.kafka.service.impl;

import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.kafka.service.KafkaFreeDriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaFreeDriverServiceImpl implements KafkaFreeDriverService {
    private final KafkaTemplate<String, DriverResponse> kafkaTemplate;
    @Value("${free-driver.topic.name}")
    private String topicName;

    @Override
    public void sendFreeDriverToConsumer(DriverResponse driver) {
        kafkaTemplate.send(topicName, driver);
    }
}