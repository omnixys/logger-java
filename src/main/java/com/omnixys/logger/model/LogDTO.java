package com.omnixys.logger.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.opentelemetry.context.Context;

import java.time.Instant;
import java.util.Map;

/**
 * Structured log entry.
 */
@JsonIgnoreProperties
public record LogDTO(
        String service,
        LogLevel level,
        String message,
        Instant timestamp,
        Map<String, Object> metadata
) {
}
