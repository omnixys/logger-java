package com.omnixys.logger.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Log level definition.
 */
@Getter
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

    @JsonValue
    public String toJson() {
        return value;
    }

    @JsonCreator
    public static LogLevel fromValue(String value) {
        for (LogLevel level : values()) {
            if (level.value.equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown LogLevel: " + value);
    }
}