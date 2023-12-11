package com.example.ridesservice.kafka.service;

import com.example.ridesservice.dto.message.RatingMessage;

public interface KafkaRatingService {
    void sendPassengerRating(RatingMessage ratingMessage);

    void sendDriverRating(RatingMessage ratingMessage);
}
