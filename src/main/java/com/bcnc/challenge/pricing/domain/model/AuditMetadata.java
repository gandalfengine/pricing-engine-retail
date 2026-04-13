package com.bcnc.challenge.pricing.domain.model;

import java.time.LocalDateTime;

public record AuditMetadata(
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}