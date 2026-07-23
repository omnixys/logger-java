package com.omnixys.logger.autoconfigure;

import com.omnixys.logger.logging.OmnixysLogger;
import com.omnixys.logger.property.LoggerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for the Omnixys observability module.
 *
 * Provides:
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
    public OmnixysLogger logger(
            LoggerProperties props
    ) {
        return new OmnixysLogger(props.getServiceName());
    }
}
