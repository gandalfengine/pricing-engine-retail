package com.bcnc.challenge.pricing.infrastructure.adapters.in.web.handler;

import com.bcnc.challenge.pricing.application.exceptions.ApplicablePriceNotFoundException;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response.ApiErrorResponse;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static com.bcnc.challenge.pricing.infrastructure.adapters.in.web.filter.CorrelationIdFilter.CORRELATION_ID_KEY;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        return badRequest(
                "Invalid request",
                "Missing required request parameter: " + ex.getParameterName(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        return badRequest(
                "Invalid request",
                "Invalid value for parameter: " + ex.getName(),
                request
        );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(
            BindException ex,
            HttpServletRequest request
    ) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> "Invalid value for parameter: " + error.getField())
                .orElse("Invalid request");

        return badRequest("Invalid request", detail, request);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ApiResponse<Void>> handleServletRequestBindingException(
            ServletRequestBindingException ex,
            HttpServletRequest request
    ) {
        return badRequest("Invalid request", ex.getMessage(), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        String detail = ex.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    String field = path.substring(path.lastIndexOf('.') + 1);
                    return "Invalid value for parameter: " + field;
                })
                .orElse("Invalid request");

        return badRequest("Invalid request", detail, request);
    }

    @ExceptionHandler(ApplicablePriceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleApplicablePriceNotFound(
            ApplicablePriceNotFoundException ex,
            HttpServletRequest request
    ) {
        String correlationId = (String) request.getAttribute(CORRELATION_ID_KEY);

        log.warn("Applicable price not found. correlationId={}, detail={}", correlationId, ex.getMessage());

        ApiErrorResponse error = new ApiErrorResponse(
                "about:blank",
                "Resource not found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure("Resource not found", correlationId, error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedError(
            Exception ex,
            HttpServletRequest request
    ) {
        String correlationId = (String) request.getAttribute(CORRELATION_ID_KEY);

        log.error("Unexpected error while processing request. correlationId={}", correlationId, ex);

        ApiErrorResponse error = new ApiErrorResponse(
                "about:blank",
                "Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Internal server error", correlationId, error));
    }

    private ResponseEntity<ApiResponse<Void>> badRequest(
            String message,
            String detail,
            HttpServletRequest request
    ) {
        String correlationId = (String) request.getAttribute(CORRELATION_ID_KEY);

        log.warn("Request validation failed. correlationId={}, detail={}", correlationId, detail);

        ApiErrorResponse error = new ApiErrorResponse(
                "about:blank",
                "Invalid request",
                HttpStatus.BAD_REQUEST.value(),
                detail,
                request.getRequestURI()
        );

        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(message, correlationId, error));
    }
}