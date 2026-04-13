package com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response;

public record ApiErrorResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance
) {
}