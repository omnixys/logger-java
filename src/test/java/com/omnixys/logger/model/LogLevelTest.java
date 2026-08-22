package com.omnixys.logger.model;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogLevelTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void fromValue_shouldBeCaseInsensitive() {
        assertEquals(LogLevel.INFO, LogLevel.fromValue("info"));
        assertEquals(LogLevel.INFO, LogLevel.fromValue("INFO"));
        assertEquals(LogLevel.INFO, LogLevel.fromValue("Info"));
        assertEquals(LogLevel.TRACE, LogLevel.fromValue("trace"));
        assertEquals(LogLevel.WARN, LogLevel.fromValue("warn"));
        assertEquals(LogLevel.ERROR, LogLevel.fromValue("error"));
        assertEquals(LogLevel.DEBUG, LogLevel.fromValue("debug"));
    }

    @Test
    void fromValue_shouldRejectUnknownLevel() {
        assertThrows(IllegalArgumentException.class, () -> LogLevel.fromValue("verbose"));
        assertThrows(IllegalArgumentException.class, () -> LogLevel.fromValue(null));
    }

    @Test
    void value_shouldReturnLowerCaseString() {
        assertEquals("info", LogLevel.INFO.getValue());
        assertEquals("trace", LogLevel.TRACE.getValue());
    }

    @Test
    void serialize_shouldWriteLowerCaseValue() throws Exception {
        String json = mapper.writeValueAsString(LogLevel.WARN);
        assertEquals("\"warn\"", json);
    }

    @Test
    void deserialize_shouldReadLowerCaseValue() throws Exception {
        LogLevel level = mapper.readValue("\"error\"", LogLevel.class);
        assertEquals(LogLevel.ERROR, level);
    }
}
