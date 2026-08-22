package com.omnixys.logger.autoconfigure;

import com.omnixys.logger.logging.OmnixysLogger;
import com.omnixys.logger.property.LoggerProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoggerAutoConfigurationTest {

    @Test
    void logger_shouldCreateBeanUsingServiceName() {
        var props = new LoggerProperties();
        props.setServiceName("orders-service");

        OmnixysLogger logger = new LoggerAutoConfiguration().logger(props);

        assertNotNull(logger);
    }

    @Test
    void logger_shouldWorkWithDefaultProperties() {
        OmnixysLogger logger = new LoggerAutoConfiguration().logger(new LoggerProperties());

        assertNotNull(logger);
        assertEquals("unknown-service", new LoggerProperties().getServiceName());
    }
}
