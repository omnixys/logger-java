package com.omnixys.logger.transport;

import com.omnixys.kafka.model.EventType;
import com.omnixys.kafka.model.KafkaEnvelope;
import com.omnixys.kafka.model.KafkaMetaData;
import com.omnixys.kafka.producer.KafkaProducerService;
import com.omnixys.logger.model.LogDTO;
import com.omnixys.observability.api.TraceContext;
import com.omnixys.observability.api.TracePropagation;
import com.omnixys.observability.api.TraceSpanKind;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static com.omnixys.logger.utils.Constants.*;

/**
 * Kafka-based implementation of LogTransport.
 * Delegates to central Kafka package.
 */
@RequiredArgsConstructor
public class KafkaLogTransport implements LogTransport {

    private final KafkaProducerService kafka;
    private final TracePropagation<?> tracing;
    private final String topic;

    @Override
    public void send(LogDTO log, TraceContext ctx) {

        tracing.runWithSpan("Kafka PRODUCE " + topic, TraceSpanKind.PRODUCER, () -> {
        // 2. Build envelope WITH metadata
        KafkaEnvelope<LogDTO> envelope = KafkaEnvelope.of(
                "Create Log",
                EventType.LOG,
                log.service(),
                VERSION_DEFAULT,
                log

        );

        KafkaMetaData meta = new KafkaMetaData(
                log.service(),
                VERSION_DEFAULT,
                log.metadata().get(CLAZZ).toString(),
                "Logging " + log.service() + "-service " + log.metadata().get(METHOD),
                EventType.LOG);

        // 3. Send
        kafka.send(topic, envelope, meta, ctx, null);
            return null;
        });
    }
}