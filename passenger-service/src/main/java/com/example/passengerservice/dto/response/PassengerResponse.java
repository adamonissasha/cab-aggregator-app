package com.example.passengerservice.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PassengerResponse {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
