package com.omnixys.logger.logging;

import com.omnixys.logger.model.LogLevel;
import com.omnixys.logger.utils.LogFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval = true)
public class OmnixysLogger {

    private final Logger logger;

    public OmnixysLogger(String service) {
        this.logger = LoggerFactory.getLogger(service);
    }

    /** Compatibility constructor for callers compiled against the former batch transport. */
    @Deprecated(forRemoval = true)
    public OmnixysLogger(String service, AsyncBatchLogger ignored) {
        this(service);
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
        switch (level) {
            case DEBUG -> logger.debug(message);
            case INFO -> logger.info(message);
            case WARN -> logger.warn(message);
            case ERROR -> logger.error(message);
            case TRACE -> logger.trace(message);
        }
    }
}
