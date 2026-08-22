package com.omnixys.logger.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogFormatterTest {

    @Test
    void format_shouldSubstitutePlaceholders() {
        assertEquals("hello world 42", LogFormatter.format("hello {} {}", "world", 42));
    }

    @Test
    void format_shouldReturnTemplateWhenNoArgs() {
        assertEquals("plain message", LogFormatter.format("plain message"));
    }

    @Test
    void format_shouldReturnTemplateWhenArgsNull() {
        assertEquals("plain message", LogFormatter.format("plain message", (Object[]) null));
    }

    @Test
    void format_shouldKeepUnfilledPlaceholders() {
        assertEquals("a b c {} d", LogFormatter.format("a {} c {} d", "b"));
    }

    @Test
    void format_shouldIgnoreExtraArgs() {
        assertEquals("a b c", LogFormatter.format("a {} c", "b", "ignored"));
    }

    @Test
    void format_shouldKeepLiteralBraces() {
        assertEquals("{}", LogFormatter.format("{}", (Object[]) null));
    }

    @Test
    void format_shouldAppendNullAsNull() {
        assertEquals("value: null", LogFormatter.format("value: {}", new Object[]{null}));
    }
}
