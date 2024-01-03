package com.example.driverservice.kafka.service;

import com.example.driverservice.dto.response.DriverResponse;

public interface KafkaFreeDriverService {
    void sendFreeDriverToConsumer(DriverResponse driver);
}