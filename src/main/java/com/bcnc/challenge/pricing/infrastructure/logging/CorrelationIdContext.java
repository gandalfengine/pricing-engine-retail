package com.bcnc.challenge.pricing.infrastructure.logging;

import org.slf4j.MDC;

import static com.bcnc.challenge.pricing.infrastructure.adapters.in.web.filter.CorrelationIdFilter.CORRELATION_ID_KEY;

public final class CorrelationIdContext {

    private CorrelationIdContext() {
    }

    public static String getCurrentCorrelationId() {
        return MDC.get(CORRELATION_ID_KEY);
    }
}