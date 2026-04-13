package com.bcnc.challenge.pricing.infrastructure.adapters.in.web.handler;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static com.bcnc.challenge.pricing.infrastructure.adapters.in.web.filter.CorrelationIdFilter.CORRELATION_ID_KEY;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParameter(MissingServletRequestParameterException ex) {
        String correlationId = MDC.get(CORRELATION_ID_KEY);

        log.warn("Missing required request parameter. parameter={}, correlationId={}",
                ex.getParameterName(), correlationId);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Missing required request parameter: " + ex.getParameterName()
        );
        problem.setTitle("Invalid request");
        problem.setProperty("correlationId", correlationId);
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String correlationId = MDC.get(CORRELATION_ID_KEY);

        log.warn("Invalid request parameter type. parameter={}, value={}, correlationId={}",
                ex.getName(), ex.getValue(), correlationId);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid value for parameter: " + ex.getName()
        );
        problem.setTitle("Invalid request");
        problem.setProperty("correlationId", correlationId);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedError(Exception ex) {
        String correlationId = MDC.get(CORRELATION_ID_KEY);

        log.error("Unexpected error while processing request. correlationId={}", correlationId, ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        problem.setTitle("Internal server error");
        problem.setProperty("correlationId", correlationId);
        return problem;
    }
}