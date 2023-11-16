package com.example.ridesservice.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExceptionResponse {
    private int statusCode;
    private String message;
}
