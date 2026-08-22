package com.omnixys.logger.property;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerPropertiesTest {

    @Test
    void shouldProvideDefaults() {
        var props = new LoggerProperties();

        assertEquals("unknown-service", props.getServiceName());
        assertTrue(props.getBatch().isEnabled());
        assertEquals(200, props.getBatch().getMaxSize());
        assertEquals(100, props.getBatch().getFlushIntervalMs());
    }

    @Test
    void shouldAllowOverrides() {
        var props = new LoggerProperties();

        props.setServiceName("orders-service");
        props.getBatch().setEnabled(false);
        props.getBatch().setMaxSize(500);
        props.getBatch().setFlushIntervalMs(250);

        assertEquals("orders-service", props.getServiceName());
        assertFalse(props.getBatch().isEnabled());
        assertEquals(500, props.getBatch().getMaxSize());
        assertEquals(250, props.getBatch().getFlushIntervalMs());
    }

    @Test
    void shouldExposeNestedBatchInstance() {
        var props = new LoggerProperties();
        assertEquals(props.getBatch(), props.getBatch());
    }
}
