package com.omnixys.logger.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Logger configuration.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "omnixys.logger")
public class LoggerProperties {

    private String serviceName = "unknown-service";

    private Batch batch = new Batch();

    @Getter
    @Setter
    public static class Batch {
        private boolean enabled = true;
        private int maxSize = 200;
        private long flushIntervalMs = 100;
    }
}