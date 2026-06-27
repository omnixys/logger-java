package com.omnixys.logger.transport;

import com.omnixys.logger.model.LogDTO;
import com.omnixys.observability.api.TraceContext;

/**
 * Transport abstraction for sending logs.
 * Allows decoupling from Kafka or other systems.
 *
 * @deprecated Replaced by standard SLF4J + MDC enrichment.
 */
@Deprecated(forRemoval = true)
public interface LogTransport {

    /**
     * Sends a log entry.
     */
    void send(LogDTO dto, TraceContext ctx);
}