package com.example.passengerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "passenger_rating")
public class PassengerRating {
    @Id
    private String id;

    @DocumentReference
    private Passenger passenger;

    private Long driverId;

    private Long rideId;

    private Integer rating;
}