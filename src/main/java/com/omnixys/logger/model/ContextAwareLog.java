package com.omnixys.logger.model;

import io.opentelemetry.context.Context;

public record ContextAwareLog(
        Context context,
        LogDTO log
) {}
