package com.example.ridesservice.kafka.service.impl;

import com.example.ridesservice.dto.response.DriverResponse;
import com.example.ridesservice.kafka.service.KafkaFreeDriverService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaFreeDriverServiceImpl implements KafkaFreeDriverService {
    private static final String REDIS_FREE_DRIVER_LIST_NAME = "freeDrivers";
    private static final String JSON_ERROR_MESSAGE = "Error processing JSON for the driver: {}";

    private final ObjectMapper objectMapper;
    private final Jedis jedis;

    @Override
    public void consumeFreeDriver(DriverResponse driverResponse) {
        try {
            String driverResponseJson = objectMapper.writeValueAsString(driverResponse);
            jedis.rpush(REDIS_FREE_DRIVER_LIST_NAME, driverResponseJson);

            log.info("Free driver added to Redis free driver list: {}", driverResponse);
        } catch (JsonProcessingException e) {
            log.error(JSON_ERROR_MESSAGE, e.getMessage());
        }
    }
}