package com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void shouldCreateSuccessResponse() {
        String message = "Success";
        String correlationId = "corr-123";
        String payload = "data";

        ApiResponse<String> response = ApiResponse.success(message, correlationId, payload);

        assertEquals(message, response.message());
        assertEquals(correlationId, response.correlationId());
        assertEquals(payload, response.payload());
        assertNull(response.error());
    }

    @Test
    void shouldCreateFailureResponse() {
        String message = "Error occurred";
        String correlationId = "corr-456";
        ApiErrorResponse error = new ApiErrorResponse(
                "about:blank",
                "Bad Request",
                400,
                "Invalid input",
                "/api/test"
        );

        ApiResponse<Void> response = ApiResponse.failure(message, correlationId, error);

        assertEquals(message, response.message());
        assertEquals(correlationId, response.correlationId());
        assertNull(response.payload());
        assertEquals(error, response.error());
    }

    @Test
    void shouldThrowExceptionWhenBothPayloadAndErrorAreProvided() {
        ApiErrorResponse error = new ApiErrorResponse(
                "about:blank",
                "Error",
                500,
                "Something went wrong",
                "/api/test"
        );

        assertThrows(IllegalArgumentException.class, () ->
                new ApiResponse<>("msg", "corr", "payload", error)
        );
    }

    @Test
    void shouldThrowExceptionWhenBothPayloadAndErrorAreNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new ApiResponse<>("msg", "corr", null, null)
        );
    }

    @Test
    void shouldAllowNullCorrelationId() {
        ApiResponse<String> response = ApiResponse.success("msg", null, "payload");

        assertNull(response.correlationId());
        assertEquals("payload", response.payload());
    }

    @Test
    void shouldAllowNullMessage() {
        ApiResponse<String> response = ApiResponse.success(null, "corr", "payload");

        assertNull(response.message());
        assertEquals("corr", response.correlationId());
    }

    @Test
    void shouldSupportGenericPayloadTypes() {
        record TestPayload(Long id, String name) {}

        TestPayload payload = new TestPayload(1L, "test");

        ApiResponse<TestPayload> response =
                ApiResponse.success("ok", "corr", payload);

        assertEquals(payload, response.payload());
        assertEquals(1L, response.payload().id());
        assertEquals("test", response.payload().name());
    }

    @Test
    void shouldVerifyEqualsAndHashCode() {
        ApiResponse<String> r1 = ApiResponse.success("msg", "corr", "data");
        ApiResponse<String> r2 = ApiResponse.success("msg", "corr", "data");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldVerifyToStringIsNotNull() {
        ApiResponse<String> response = ApiResponse.success("msg", "corr", "data");

        assertNotNull(response.toString());
    }
}