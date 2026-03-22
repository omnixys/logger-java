package com.omnixys.logger.transport;

import com.omnixys.kafka.model.KafkaEnvelope;
import com.omnixys.kafka.producer.KafkaProducerService;
import com.omnixys.logger.model.LogDTO;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka-based implementation of LogTransport.
 * Delegates to central Kafka package.
 */
@RequiredArgsConstructor
public class KafkaLogTransport implements LogTransport {

    private final KafkaProducerService kafka;
    private final String topic;

    @Override
    public void send(LogDTO log) {

        // 1. Copy metadata (VERY IMPORTANT → avoid mutation)
        Map<String, String> metadata =
                log.metadata() != null
                        ? new HashMap<>(log.metadata())
                        : new HashMap<>();

        // 2. Build envelope WITH metadata
        KafkaEnvelope<LogDTO> envelope = KafkaEnvelope.of(
                "log.created",
                "log",
                log.service(),
                "v1",
                log,
                metadata
        );

        // 3. Send
        kafka.send(topic, envelope);
    }
}