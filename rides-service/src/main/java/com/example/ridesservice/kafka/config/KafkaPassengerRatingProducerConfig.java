package com.example.ridesservice.kafka.config;

import com.example.ridesservice.dto.message.RatingMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.MessageChannel;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaPassengerRatingProducerConfig {
    private final KafkaProperties kafkaProperties;

    @Value("${topic.name.rate.passenger}")
    private String passengerRatingTopicName;

    @Bean
    public IntegrationFlow sendPassengerRatingToKafkaFlow() {
        return f -> f.channel("passengerRatingKafkaChannel")
                .handle(Kafka.outboundChannelAdapter(passengerRatingKafkaTemplate())
                        .messageKey(m -> m.getHeaders().get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER))
                        .topic(passengerRatingTopicName));
    }

    @Bean
    public MessageChannel passengerRatingKafkaChannel() {
        return new DirectChannel();
    }

    @Bean
    public KafkaTemplate<String, String> passengerRatingKafkaTemplate() {
        return new KafkaTemplate<>(passengerRatingProducerFactory());
    }

    @Bean
    public ProducerFactory<String, String> passengerRatingProducerFactory() {
        Map<String, Object> properties = kafkaProperties.buildProducerProperties();
        properties.put(JsonSerializer.TYPE_MAPPINGS, "ratingMessage:" + RatingMessage.class.getName());
        return new DefaultKafkaProducerFactory<>(properties);
    }
}