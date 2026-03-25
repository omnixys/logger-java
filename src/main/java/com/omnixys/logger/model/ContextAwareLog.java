package com.omnixys.logger.model;

import com.omnixys.observability.api.TraceContext;
import com.omnixys.observability.api.TraceContextSnapshot;
import io.opentelemetry.context.Context;

public record ContextAwareLog(
        TraceContextSnapshot snapshot,
        LogDTO log,
        TraceContext traceContext
) {}
