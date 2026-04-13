package com.bcnc.challenge.pricing.application.exceptions;

public class ApplicablePriceNotFoundException extends RuntimeException {
    public ApplicablePriceNotFoundException(String message) {
        super(message);
    }
}