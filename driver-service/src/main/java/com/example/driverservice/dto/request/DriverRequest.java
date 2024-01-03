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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DriverRequest {
    @NotBlank(message = "{driver.first-name.required}")
    private String firstName;

    @NotBlank(message = "{driver.last-name.required}")
    private String lastName;

    @NotBlank(message = "{driver.email.required}")
    @Email(message = "{driver.email.format}")
    private String email;

    @NotBlank(message = "{driver.phone-number.required}")
    @Pattern(regexp = "^\\+375\\d{9}$", message = "{driver.phone-number.format}")
    private String phoneNumber;

    @NotBlank(message = "{driver.password.required}")
    @Pattern(regexp = "^(?=.*\\d).{8,}$", message = "{driver.password.format}")
    private String password;

    @NotNull(message = "{driver.car-id.not-null}")
    private Long carId;
}
