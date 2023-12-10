package com.example.ridesservice.kafka.service.impl;

import com.example.ridesservice.dto.response.DriverResponse;
import com.example.ridesservice.kafka.service.KafkaFreeDriverService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@RequiredArgsConstructor
public class KafkaFreeDriverServiceImpl implements KafkaFreeDriverService {
    private static final String REDIS_FREE_DRIVER_LIST_NAME = "freeDrivers";
    private final ObjectMapper objectMapper;
    private final Jedis jedis;

    @Override
    public void consumeFreeDriver(DriverResponse driverResponse) {
        try {
            String driverResponseJson = objectMapper.writeValueAsString(driverResponse);
            jedis.rpush(REDIS_FREE_DRIVER_LIST_NAME, driverResponseJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}