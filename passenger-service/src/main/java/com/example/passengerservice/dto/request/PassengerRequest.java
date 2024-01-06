package com.example.passengerservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PassengerRequest {
    private static final String PASSENGER_PHONE_NUMBER_FORMAT = "^\\+375\\d{9}$";
    private static final String PASSENGER_PASSWORD_FORMAT =  "^(?=.*\\d).{8,}$";

    @NotBlank(message = "{passenger.first-name.required}")
    private String firstName;

    @NotBlank(message = "{passenger.last-name.required}")
    private String lastName;

    @NotBlank(message = "{passenger.email.required}")
    @Email(message = "{passenger.email.format}")
    private String email;

    @NotBlank(message = "{passenger.phone-number.required}")
    @Pattern(regexp = PASSENGER_PHONE_NUMBER_FORMAT, message = "{passenger.phone-number.format}")
    private String phoneNumber;

    @NotBlank(message = "{passenger.password.required}")
    @Pattern(regexp = PASSENGER_PASSWORD_FORMAT, message = "{passenger.password.format}")
    private String password;
}
