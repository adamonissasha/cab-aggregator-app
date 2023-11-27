package com.example.ridesservice.model.enums;

import java.util.Arrays;

public enum PaymentMethod {
    CARD,
    CASH;

    public static boolean isValidPaymentMethod(String text) {
        return Arrays.stream(PaymentMethod.values())
                .anyMatch(method -> method.name().equalsIgnoreCase(text));
    }
}
