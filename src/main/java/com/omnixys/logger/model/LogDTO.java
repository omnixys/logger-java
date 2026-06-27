package com.omnixys.logger.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.omnixys.commons.model.TraceContext;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LogDTO(
        String service,
        LogLevel level,
        String message,
        Instant timestamp,
        Map<String, Object> metadata,
        TraceContext traceContext,
        String operation
) {
}
