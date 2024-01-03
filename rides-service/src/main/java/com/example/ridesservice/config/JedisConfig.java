package com.example.ridesservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class JedisConfig {
    @Value("${jedis.port}")
    private int jedisPort;
    @Value("${jedis.host}")
    private String jedisHost;

    @Bean
    public Jedis jedis() {
        return new Jedis(jedisHost, jedisPort);
    }
}