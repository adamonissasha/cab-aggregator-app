package com.example.ridesservice.kafka.service;

import com.example.ridesservice.dto.message.RatingMessage;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaSendRatingGateway {
    @Gateway(requestChannel = "passengerRatingKafkaChannel")
    void sendPassengerRating(RatingMessage ratingMessage);

    @Gateway(requestChannel = "driverRatingKafkaChannel")
    void sendDriverRating(RatingMessage ratingMessage);
}