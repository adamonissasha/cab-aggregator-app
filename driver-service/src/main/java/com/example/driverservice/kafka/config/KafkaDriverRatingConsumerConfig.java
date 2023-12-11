package com.example.driverservice.kafka.config;

import com.example.driverservice.dto.request.DriverRatingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaDriverRatingConsumerConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, DriverRatingRequest> consumerFactory() {
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
        properties.put(JsonSerializer.TYPE_MAPPINGS, "ratingMessage:" + DriverRatingRequest.class.getName());
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DriverRatingRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DriverRatingRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}