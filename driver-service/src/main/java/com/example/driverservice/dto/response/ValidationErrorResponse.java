package com.example.driverservice.dto.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class ValidationErrorResponse {
    private  List<String> errors;
    private int statusCode;
}