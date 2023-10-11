package com.example.payment.enums;

import java.util.Locale;
import java.util.Optional;

public enum PaymentStatus {
    PAYMENT_PROCESSING,
    PAYMENT_SUCCESS,
    PAYMENT_FAIL,
    ;


    public static PaymentStatus fromString(String value) {
        try {
            return valueOf(value.toUpperCase(Locale.US));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Invalid value '%s' for PaymentStatus (case insensitive)", value), e);
        }
    }

    public static Optional<PaymentStatus> fromOptionalString(String value) {
        try {
            return Optional.of(fromString(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
