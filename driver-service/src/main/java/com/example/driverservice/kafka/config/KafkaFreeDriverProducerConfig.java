package com.example.driverservice.kafka.config;

import com.example.driverservice.dto.response.DriverResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaFreeDriverProducerConfig {
    private final KafkaProperties kafkaProperties;
    private static final String FREE_DRIVER_MESSAGE = "freeDriverMessage:";
    private static final int PARTITIONS_COUNT = 1;
    private static final int REPLICAS_COUNT = 1;
    @Value("${topic.name.free-driver}")
    private String topicName;

    @Bean
    public ProducerFactory<String, DriverResponse> producerFactory() {
        Map<String, Object> properties = kafkaProperties.buildProducerProperties();
        properties.put(JsonSerializer.TYPE_MAPPINGS, FREE_DRIVER_MESSAGE + DriverResponse.class.getName());
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, DriverResponse> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic sendFreeDriverTopic() {
        return TopicBuilder.name(topicName)
                .partitions(PARTITIONS_COUNT)
                .replicas(REPLICAS_COUNT)
                .build();
    }
}