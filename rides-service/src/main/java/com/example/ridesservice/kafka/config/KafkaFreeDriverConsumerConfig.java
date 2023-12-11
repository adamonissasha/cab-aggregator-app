package com.example.ridesservice.kafka.config;

import com.example.ridesservice.dto.response.DriverResponse;
import com.example.ridesservice.kafka.service.KafkaFreeDriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaFreeDriverConsumerConfig {
    private final KafkaProperties kafkaProperties;
    private final KafkaFreeDriverService kafkaFreeDriverService;
    @Value("${topic.name.free-driver}")
    private String topicName;

    @Bean
    public IntegrationFlow consumeFromKafka(ConsumerFactory<String, String> consumerFactory) {
        return IntegrationFlow.from(Kafka.messageDrivenChannelAdapter(consumerFactory, topicName))
                .handle(kafkaFreeDriverService, "consumeFreeDriver")
                .get();
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
        properties.put(JsonDeserializer.TYPE_MAPPINGS, "driverResponse:" + DriverResponse.class.getName());
        return new DefaultKafkaConsumerFactory<>(properties);
    }
}
