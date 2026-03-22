package com.omnixys.logger.autoconfigure;

import com.omnixys.kafka.producer.KafkaProducerService;
import com.omnixys.logger.logging.AsyncBatchLogger;
import com.omnixys.logger.logging.OmnixysLogger;
import com.omnixys.logger.property.LoggerProperties;
import com.omnixys.logger.transport.KafkaLogTransport;
import com.omnixys.logger.transport.LogTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for the Omnixys observability module.
 *
 * Provides:
 * - Kafka-based log transport (optional)
 * - Batch logging (optional)
 * - Root logger
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(LoggerProperties.class)
@ConditionalOnProperty(
        prefix = "omnixys.logger",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class LoggerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(KafkaProducerService.class)
    public LogTransport transport(
            KafkaProducerService producer,
            LoggerProperties props
    ) {
        return new KafkaLogTransport(
                producer,
                props.getKafka().getTopic()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public AsyncBatchLogger batch(
            LogTransport transport,
            LoggerProperties props
    ) {
        return new AsyncBatchLogger(
                transport,
                props.getBatch().getMaxSize(),
                props.getBatch().getFlushIntervalMs()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public OmnixysLogger logger(
            LoggerProperties props,
            AsyncBatchLogger batch
    ) {
        return new OmnixysLogger(
                props.getServiceName(),
                batch
        );
    }
}