package com.omnixys.logger.model;

import io.opentelemetry.context.Context;

import java.time.Instant;
import java.util.Map;

/**
 * Structured log entry.
 */
public record LogDTO(
        String service,
        LogLevel level,
        String message,
        Instant timestamp,
        Map<String, String> metadata
) {
}
