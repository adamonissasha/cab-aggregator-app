package com.example.driverservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DriverRequest {
    private static final String DRIVER_PHONE_NUMBER_FORMAT = "^\\+375\\d{9}$";
    private static final String DRIVER_PASSWORD_FORMAT = "^(?=.*\\d).{8,}$";

    @NotBlank(message = "{driver.first-name.required}")
    private String firstName;

    @NotBlank(message = "{driver.last-name.required}")
    private String lastName;

    @NotBlank(message = "{driver.email.required}")
    @Email(message = "{driver.email.format}")
    private String email;

    @NotBlank(message = "{driver.phone-number.required}")
    @Pattern(regexp = DRIVER_PHONE_NUMBER_FORMAT, message = "{driver.phone-number.format}")
    private String phoneNumber;

    @NotBlank(message = "{driver.password.required}")
    @Pattern(regexp = DRIVER_PASSWORD_FORMAT, message = "{driver.password.format}")
    private String password;

    @NotNull(message = "{driver.car-id.not-null}")
    private Long carId;
}
