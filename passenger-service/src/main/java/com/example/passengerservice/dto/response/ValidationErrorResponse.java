package com.example.passengerservice.dto.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class ValidationErrorResponse {
    private  List<String> errors;
    private int statusCode;
}