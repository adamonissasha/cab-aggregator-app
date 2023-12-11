package com.example.ridesservice.kafka.service;

import com.example.ridesservice.dto.message.RatingMessage;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaSendRatingGateway extends KafkaRatingService {
    @Override
    @Gateway(requestChannel = "passengerRatingKafkaChannel")
    void sendPassengerRating(RatingMessage ratingMessage);

    @Override
    @Gateway(requestChannel = "driverRatingKafkaChannel")
    void sendDriverRating(RatingMessage ratingMessage);
}