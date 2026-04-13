package com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String message,
        String correlationId,
        T payload,
        ApiErrorResponse error
) {

    public ApiResponse {
        if (payload != null && error != null) {
            throw new IllegalArgumentException("ApiResponse cannot have both payload and error");
        }

        if (payload == null && error == null) {
            throw new IllegalArgumentException("ApiResponse must have either payload or error");
        }
    }

    public static <T> ApiResponse<T> success(String message, String correlationId, T payload) {
        return new ApiResponse<>(message, correlationId, payload, null);
    }

    public static <T> ApiResponse<T> failure(String message, String correlationId, ApiErrorResponse error) {
        return new ApiResponse<>(message, correlationId, null, error);
    }
}