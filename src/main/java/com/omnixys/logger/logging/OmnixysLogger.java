package com.omnixys.logger.logging;

import com.omnixys.logger.model.LogDTO;
import com.omnixys.logger.model.LogLevel;
import com.omnixys.logger.utils.LogFormatter;
import com.omnixys.logger.utils.StackWalkerUtil;
import com.omnixys.observability.context.ITraceContext;
import com.omnixys.observability.context.TraceContextExtractor;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.omnixys.logger.utils.Constants.*;

@Slf4j
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

//        if (trace.isValid()) {
//            metadata.put(TRACE_ID, trace.traceId());
//            metadata.put(SPAN_ID, trace.spanId());
//            metadata.put(SAMPLED, String.valueOf(trace.sampled()));
//        }

        log.info("traceId: {}", trace.traceId());
        log.info("spanId: {}", trace.spanId());
        log.info("sampled: {}", trace.sampled());

        // optional: add method/class also into metadata (for consistency with Kafka)
        if (caller.method() != null) metadata.put(METHOD, caller.method());
        if (caller.clazz() != null) metadata.put(CLAZZ, caller.clazz());

        LogDTO dto = new LogDTO(
                service,
                level,
                message,
                Instant.now(),
                metadata


        );

        batch.submit(dto);
    }
}