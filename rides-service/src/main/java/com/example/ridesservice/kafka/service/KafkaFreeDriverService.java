package com.example.ridesservice.kafka.service;

import com.example.ridesservice.dto.response.DriverResponse;

public interface KafkaFreeDriverService {
    void consumeFreeDriver(DriverResponse driverResponse);
}