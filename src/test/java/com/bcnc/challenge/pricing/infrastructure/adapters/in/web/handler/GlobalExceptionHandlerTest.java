package com.bcnc.challenge.pricing.infrastructure.adapters.in.web.handler;

import com.bcnc.challenge.pricing.application.exceptions.ApplicablePriceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static com.bcnc.challenge.pricing.infrastructure.adapters.in.web.filter.CorrelationIdFilter.CORRELATION_ID_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();

        request = new MockHttpServletRequest();
        request.setRequestURI("/api/prices");
        request.setAttribute(CORRELATION_ID_KEY, "test-correlation-id");
    }

    @Test
    void shouldHandleMissingServletRequestParameterException() throws Exception {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("applicationDate", "LocalDateTime");

        ResponseEntity<?> response = globalExceptionHandler.handleMissingParameter(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = toMap(response);
        assertEquals("Invalid request", body.get("message"));
        assertEquals("test-correlation-id", body.get("correlationId"));

        Map<String, Object> error = getError(body);
        assertEquals("Invalid request", error.get("title"));
        assertEquals(400, error.get("status"));
        assertEquals("Missing required request parameter: applicationDate", error.get("detail"));
        assertEquals("/api/prices", error.get("instance"));
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatchException() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "invalid-date",
                LocalDateTime.class,
                "applicationDate",
                (MethodParameter) null,
                null
        );

        ResponseEntity<?> response = globalExceptionHandler.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = toMap(response);
        assertEquals("Invalid request", body.get("message"));
        assertEquals("test-correlation-id", body.get("correlationId"));

        Map<String, Object> error = getError(body);
        assertEquals("Invalid request", error.get("title"));
        assertEquals(400, error.get("status"));
        assertEquals("Invalid value for parameter: applicationDate", error.get("detail"));
        assertEquals("/api/prices", error.get("instance"));
    }

    @Test
    void shouldHandleBindException() {
        TestCriteria target = new TestCriteria();
        DataBinder binder = new DataBinder(target, "criteria");
        binder.getBindingResult().rejectValue("productId", "typeMismatch", "Invalid value");
        BindException ex = new BindException(binder.getBindingResult());

        ResponseEntity<?> response = globalExceptionHandler.handleBindException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = toMap(response);
        assertEquals("Invalid request", body.get("message"));
        assertEquals("test-correlation-id", body.get("correlationId"));

        Map<String, Object> error = getError(body);
        assertEquals("Invalid request", error.get("title"));
        assertEquals(400, error.get("status"));
        assertEquals("Invalid value for parameter: productId", error.get("detail"));
        assertEquals("/api/prices", error.get("instance"));
    }

    @Getter
    private static class TestCriteria {
        private Long productId;

        public void setProductId(Long productId) {
            this.productId = productId;
        }
    }

    @Test
    void shouldHandleServletRequestBindingException() {
        ServletRequestBindingException ex =
                new ServletRequestBindingException("Missing request header 'X-Correlation-Id'");

        ResponseEntity<?> response = globalExceptionHandler.handleServletRequestBindingException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = toMap(response);
        assertEquals("Invalid request", body.get("message"));
        assertEquals("test-correlation-id", body.get("correlationId"));

        Map<String, Object> error = getError(body);
        assertEquals("Invalid request", error.get("title"));
        assertEquals(400, error.get("status"));
        assertEquals("Missing request header 'X-Correlation-Id'", error.get("detail"));
        assertEquals("/api/prices", error.get("instance"));
    }

    @Test
    void shouldHandleConstraintViolationException() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("execute.productId");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<?> response = globalExceptionHandler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = toMap(response);
        assertEquals("Invalid request", body.get("message"));
        assertEquals("test-correlation-id", body.get("correlationId"));

        Map<String, Object> error = getError(body);
        assertEquals("Invalid request", error.get("title"));
        assertEquals(400, error.get("status"));
        assertEquals("Invalid value for parameter: productId", error.get("detail"));
        assertEquals("/api/prices", error.get("instance"));
    }

    @Test
    void shouldHandleApplicablePriceNotFoundException() {
        ApplicablePriceNotFoundException ex =
                new ApplicablePriceNotFoundException("No applicable price found for productId=35455");

        ResponseEntity<?> response = globalExceptionHandler.handleApplicablePriceNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Map<String, Object> body = toMap(response);
        assertEquals("Resource not found", body.get("message"));
        assertEquals("test-correlation-id", body.get("correlationId"));

        Map<String, Object> error = getError(body);
        assertEquals("Resource not found", error.get("title"));
        assertEquals(404, error.get("status"));
        assertEquals("No applicable price found for productId=35455", error.get("detail"));
        assertEquals("/api/prices", error.get("instance"));
    }

    @Test
    void shouldHandleUnexpectedException() {
        Exception ex = new RuntimeException("Unexpected failure");

        ResponseEntity<?> response = globalExceptionHandler.handleUnexpectedError(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Map<String, Object> body = toMap(response);
        assertEquals("Internal server error", body.get("message"));
        assertEquals("test-correlation-id", body.get("correlationId"));

        Map<String, Object> error = getError(body);
        assertEquals("Internal server error", error.get("title"));
        assertEquals(500, error.get("status"));
        assertEquals("An unexpected error occurred", error.get("detail"));
        assertEquals("/api/prices", error.get("instance"));
    }

    @Test
    void shouldHandleBadRequestWithoutCorrelationId() throws Exception {
        MockHttpServletRequest requestWithoutCorrelationId = new MockHttpServletRequest();
        requestWithoutCorrelationId.setRequestURI("/api/prices");

        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("brandId", "Long");

        ResponseEntity<?> response = globalExceptionHandler.handleMissingParameter(ex, requestWithoutCorrelationId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = toMap(response);
        assertEquals("Invalid request", body.get("message"));
        assertNull(body.get("correlationId"));

        Map<String, Object> error = getError(body);
        assertEquals("Missing required request parameter: brandId", error.get("detail"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(ResponseEntity<?> response) {
        assertNotNull(response.getBody());
        return (Map<String, Object>) new com.fasterxml.jackson.databind.ObjectMapper()
                .convertValue(response.getBody(), Map.class);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getError(Map<String, Object> body) {
        Object error = body.get("error");
        assertNotNull(error);
        return (Map<String, Object>) error;
    }
}