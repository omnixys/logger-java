package com.omnixys.logger.model;

import com.omnixys.commons.model.TraceContext;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogDTOTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldExposeRecordAccessors() {
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        var dto = new LogDTO("svc", LogLevel.INFO, "msg", now, Map.of("k", "v"), null, "op");

        assertEquals("svc", dto.service());
        assertEquals(LogLevel.INFO, dto.level());
        assertEquals("msg", dto.message());
        assertEquals(now, dto.timestamp());
        assertEquals(Map.of("k", "v"), dto.metadata());
        assertEquals("op", dto.operation());
    }

    @Test
    void serialize_shouldWriteLevelAsLowerCaseValue() throws Exception {
        var dto = new LogDTO("svc", LogLevel.INFO, "msg", Instant.parse("2025-01-01T00:00:00Z"),
                null, null, null);

        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"service\":\"svc\""));
        assertTrue(json.contains("\"level\":\"info\""));
        assertTrue(json.contains("\"message\":\"msg\""));
        assertTrue(json.contains("\"timestamp\":\"2025-01-01T00:00:00Z\""));
        assertFalse(json.contains("\"metadata\""));
        assertFalse(json.contains("\"traceContext\""));
        assertFalse(json.contains("\"operation\""));
    }

    @Test
    void serialize_shouldIncludeMetadataAndTraceContext() throws Exception {
        var dto = new LogDTO("svc", LogLevel.ERROR, "boom",
                Instant.parse("2025-01-01T00:00:00Z"),
                Map.of("tenant", "t-1"),
                new TraceContext("trace-1", "span-1", "parent-1", "1"),
                "run");

        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"metadata\":{\"tenant\":\"t-1\"}"));
        assertTrue(json.contains("\"traceContext\":{"));
        assertTrue(json.contains("\"operation\":\"run\""));
    }

    @Test
    void shouldBeEqualForSameValues() {
        var a = new LogDTO("svc", LogLevel.INFO, "msg", null, null, null, null);
        var b = new LogDTO("svc", LogLevel.INFO, "msg", null, null, null, null);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldDifferByLevel() {
        var a = new LogDTO("svc", LogLevel.INFO, "msg", null, null, null, null);
        var b = new LogDTO("svc", LogLevel.DEBUG, "msg", null, null, null, null);

        assertFalse(a.equals(b));
    }
}
