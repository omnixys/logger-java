package com.omnixys.logger.logging;

import com.omnixys.context.ContextAccessor;
import com.omnixys.context.ContextSnapshot;
import com.omnixys.logger.model.LogDTO;
import com.omnixys.logger.model.LogLevel;
import com.omnixys.logger.utils.LogFormatter;
import com.omnixys.logger.utils.StackWalkerUtil;
import com.omnixys.observability.context.ITraceContext;
import com.omnixys.observability.context.TraceContextExtractor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.omnixys.logger.utils.Constants.*;

@Slf4j
@Deprecated(forRemoval = true)
public class OmnixysLogger {

    private final String service;
    private final AsyncBatchLogger batch;

    public OmnixysLogger(String service, AsyncBatchLogger batch) {
        this.service = service;
        this.batch = batch;
    }

    public void debug(String template, Object... args) {
        log(LogLevel.DEBUG, template, args);
    }

    public void info(String template, Object... args) {
        log(LogLevel.INFO, template, args);
    }

    public void error(String template, Object... args) {
        log(LogLevel.ERROR, template, args);
    }

    public void warn(String template, Object... args) {
        log(LogLevel.WARN, template, args);
    }

    private void log(LogLevel level, String template, Object... args) {
        String message = LogFormatter.format(template, args);

        var caller = StackWalkerUtil.resolve();

        ITraceContext trace = TraceContextExtractor.current();

        Map<String, Object> metadata = new HashMap<>();

        if (trace.isValid()) {
            metadata.put(TRACE_ID, trace.traceId());
            metadata.put(SPAN_ID, trace.spanId());
            metadata.put(SAMPLED, String.valueOf(trace.sampled()));
        }

        ContextSnapshot ctx = ContextAccessor.get();
        if (ctx != null) {
            if (ctx.correlationId() != null) metadata.put(CORRELATION_ID, ctx.correlationId());
            if (ctx.tenant() != null) metadata.put(TENANT_ID, ctx.tenant().tenantId());
            if (ctx.principal() != null && ctx.principal().actorId() != null) {
                metadata.put(ACTOR_ID, ctx.principal().actorId());
            }
        }

        if (caller.method() != null) metadata.put(METHOD, caller.method());
        if (caller.clazz() != null) metadata.put(CLAZZ, caller.clazz());

        LogDTO dto = new LogDTO(
                service,
                level,
                message,
                Instant.now(),
                metadata,
                null,
                null
        );

        batch.submit(dto);
    }
}