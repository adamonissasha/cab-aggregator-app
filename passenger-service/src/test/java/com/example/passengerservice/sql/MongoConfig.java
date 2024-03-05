package com.example.passengerservice.sql;

import com.mongodb.ConnectionString;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;

@Configuration
public class MongoConfig {
    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(
                new SimpleReactiveMongoDatabaseFactory(
                        new ConnectionString("mongodb://localhost/passenger_test_db")
                )
        );
    }
}
