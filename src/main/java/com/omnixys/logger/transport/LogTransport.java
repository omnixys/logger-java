package com.omnixys.logger.transport;

import com.omnixys.logger.model.LogDTO;

/**
 * Transport abstraction for sending logs.
 * Allows decoupling from Kafka or other systems.
 */
public interface LogTransport {

    /**
     * Sends a log entry.
     */
    void send(LogDTO dto);
}