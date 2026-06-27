package com.omnixys.logger.model;

import lombok.Getter;


/**
 * Log level definition.
 */
@Getter
@tools.jackson.databind.annotation.JsonSerialize(using = LogLevel.LogLevelSerializer.class)
@tools.jackson.databind.annotation.JsonDeserialize(using = LogLevel.LogLevelDeserializer.class)
public enum LogLevel {

    TRACE("trace"),
    DEBUG("debug"),
    INFO("info"),
    WARN("warn"),
    ERROR("error");

    private final String value;

    LogLevel(String value) {
        this.value = value;
    }

    public static LogLevel fromValue(String value) {
        for (LogLevel level : values()) {
            if (level.value.equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown LogLevel: " + value);
    }

    public static class LogLevelSerializer extends tools.jackson.databind.ser.std.StdSerializer<LogLevel> {
        public LogLevelSerializer() { super(LogLevel.class); }
        @Override
        public void serialize(
                LogLevel value,
                tools.jackson.core.JsonGenerator gen,
                tools.jackson.databind.SerializationContext context
        ) throws tools.jackson.core.JacksonException {
            gen.writeString(value.value);
        }
    }

    public static class LogLevelDeserializer extends tools.jackson.databind.deser.std.StdDeserializer<LogLevel> {
        public LogLevelDeserializer() { super(LogLevel.class); }
        @Override
        public LogLevel deserialize(
                tools.jackson.core.JsonParser p,
                tools.jackson.databind.DeserializationContext ctxt
        ) throws tools.jackson.core.JacksonException {
            return LogLevel.fromValue(p.getValueAsString());
        }
    }
}
