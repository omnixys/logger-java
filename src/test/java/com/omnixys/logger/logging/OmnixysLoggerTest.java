package com.omnixys.logger.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class OmnixysLoggerTest {

    private static final String SERVICE = "test-service";

    @Mock
    private AsyncBatchLogger batch;

    @Mock
    private Logger delegate;

    @Test
    void delegatesFormattedMessagesToSlf4j() {
        try (MockedStatic<LoggerFactory> factory = mockStatic(LoggerFactory.class)) {
            factory.when(() -> LoggerFactory.getLogger(SERVICE)).thenReturn(delegate);

            var logger = new OmnixysLogger(SERVICE);
            logger.info("hello {} {}", "world", 42);

            verify(delegate).info("hello world 42");
        }
    }

    @Test
    void compatibilityConstructorNoLongerUsesBatchTransport() {
        try (MockedStatic<LoggerFactory> factory = mockStatic(LoggerFactory.class)) {
            factory.when(() -> LoggerFactory.getLogger(SERVICE)).thenReturn(delegate);

            var logger = new OmnixysLogger(SERVICE, batch);
            logger.warn("simple message");

            verify(delegate).warn("simple message");
            verifyNoInteractions(batch);
        }
    }

    @Test
    void delegatesDebugMessages() {
        try (MockedStatic<LoggerFactory> factory = mockStatic(LoggerFactory.class)) {
            factory.when(() -> LoggerFactory.getLogger(SERVICE)).thenReturn(delegate);

            var logger = new OmnixysLogger(SERVICE);
            logger.debug("debug {}", 1);

            verify(delegate).debug("debug 1");
        }
    }

    @Test
    void delegatesErrorMessages() {
        try (MockedStatic<LoggerFactory> factory = mockStatic(LoggerFactory.class)) {
            factory.when(() -> LoggerFactory.getLogger(SERVICE)).thenReturn(delegate);

            var logger = new OmnixysLogger(SERVICE);
            var cause = new RuntimeException("cause");
            logger.error("boom {}", cause);

            verify(delegate).error("boom " + cause);
        }
    }
}
