package com.example.passengerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "passenger")
public class Passenger {
    @Id
    private String id;

    private String firstName;

    private String lastName;

    private String email;

    @Indexed(unique = true)
    private String phoneNumber;

    private String password;

    private boolean isActive;
}
