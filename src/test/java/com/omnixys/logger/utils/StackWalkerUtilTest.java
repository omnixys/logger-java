package com.omnixys.logger.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StackWalkerUtilTest {

    @Test
    void resolve_shouldReturnCallerWithClassAndMethod() {
        StackWalkerUtil.Caller caller = StackWalkerUtil.resolve();

        assertNotNull(caller);
        assertFalse(caller.clazz().isBlank(), "clazz must not be blank");
        assertFalse(caller.method().isBlank(), "method must not be blank");
    }
}
