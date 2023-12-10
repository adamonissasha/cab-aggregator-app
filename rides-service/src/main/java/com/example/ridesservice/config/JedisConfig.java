package com.example.ridesservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class JedisConfig {
    private static final int JEDIS_PORT = 6379;
    private static final String JEDIS_HOST = "localhost";

    @Bean
    public Jedis jedis() {
        return new Jedis(JEDIS_HOST, JEDIS_PORT);
    }
}