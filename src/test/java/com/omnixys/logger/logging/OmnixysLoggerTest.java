package com.omnixys.logger.logging;

import com.omnixys.context.ClientMetadata;
import com.omnixys.context.ContextAccessor;
import com.omnixys.context.ContextSnapshot;
import com.omnixys.context.PrincipalContext;
import com.omnixys.context.TenantContext;
import com.omnixys.context.TransportMetadata;
import com.omnixys.logger.model.LogDTO;
import com.omnixys.logger.utils.Constants;
import com.omnixys.logger.utils.StackWalkerUtil;
import com.omnixys.observability.context.ITraceContext;
import com.omnixys.observability.context.TraceContextExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OmnixysLoggerTest {

    private static final String SERVICE = "test-service";
    private static final String TRACE_ID = "abc123";
    private static final String SPAN_ID = "def456";
    private static final String CORRELATION_ID = "corr-001";
    private static final String TENANT_ID = "tenant-1";
    private static final String ACTOR_ID = "actor-1";
    private static final String CALLER_CLASS = "com.omnixys.logger.logging.OmnixysLoggerTest";
    private static final String CALLER_METHOD = "testMethod";

    @Mock
    private AsyncBatchLogger batch;

    @Captor
    private ArgumentCaptor<LogDTO> logCaptor;

    @Test
    void includesContextDataWhenSnapshotPresent() {
        var snapshot = new ContextSnapshot(
                "req-1", CORRELATION_ID, System.currentTimeMillis(),
                new TenantContext(TENANT_ID, "api", true),
                new PrincipalContext("sub-1", ACTOR_ID, "user-1", TENANT_ID,
                        java.util.List.of(), "sess-1", "strong", 1000L),
                new ClientMetadata(null, null, null, null, null, null, null, null, null),
                new TransportMetadata("http", null, null, null, null, null, null,
                        null, null, null, null, null, null),
                null
        );

        var trace = mock(ITraceContext.class);
        when(trace.isValid()).thenReturn(true);
        when(trace.traceId()).thenReturn(TRACE_ID);
        when(trace.spanId()).thenReturn(SPAN_ID);
        when(trace.sampled()).thenReturn(true);

        var caller = new StackWalkerUtil.Caller(CALLER_CLASS, CALLER_METHOD);

        try (var ctx = mockStatic(ContextAccessor.class);
             var trc = mockStatic(TraceContextExtractor.class);
             var stk = mockStatic(StackWalkerUtil.class)) {

            ctx.when(ContextAccessor::get).thenReturn(snapshot);
            trc.when(TraceContextExtractor::current).thenReturn(trace);
            stk.when(StackWalkerUtil::resolve).thenReturn(caller);

            var logger = new OmnixysLogger(SERVICE, batch);
            logger.info("hello {} {}", "world", 42);

            verify(batch).submit(logCaptor.capture());
        }

        var log = logCaptor.getValue();

        assertEquals(SERVICE, log.service());
        assertEquals("hello world 42", log.message());
        assertNotNull(log.timestamp());

        var meta = log.metadata();
        assertNotNull(meta);
        assertEquals(TRACE_ID, meta.get(Constants.TRACE_ID));
        assertEquals(SPAN_ID, meta.get(Constants.SPAN_ID));
        assertEquals("true", meta.get(Constants.SAMPLED));
        assertEquals(CORRELATION_ID, meta.get(Constants.CORRELATION_ID));
        assertEquals(TENANT_ID, meta.get(Constants.TENANT_ID));
        assertEquals(ACTOR_ID, meta.get(Constants.ACTOR_ID));
        assertEquals(CALLER_CLASS, meta.get(Constants.CLAZZ));
        assertEquals(CALLER_METHOD, meta.get(Constants.METHOD));
        assertEquals(8, meta.size());
    }

    @Test
    void worksWithoutContextData() {
        var trace = mock(ITraceContext.class);
        when(trace.isValid()).thenReturn(false);

        var caller = new StackWalkerUtil.Caller(CALLER_CLASS, CALLER_METHOD);

        try (var ctx = mockStatic(ContextAccessor.class);
             var trc = mockStatic(TraceContextExtractor.class);
             var stk = mockStatic(StackWalkerUtil.class)) {

            ctx.when(ContextAccessor::get).thenReturn(null);
            trc.when(TraceContextExtractor::current).thenReturn(trace);
            stk.when(StackWalkerUtil::resolve).thenReturn(caller);

            var logger = new OmnixysLogger(SERVICE, batch);
            logger.warn("simple message");

            verify(batch).submit(logCaptor.capture());
        }

        var log = logCaptor.getValue();

        assertEquals(SERVICE, log.service());
        assertEquals("simple message", log.message());

        var meta = log.metadata();
        assertNotNull(meta);
        assertNull(meta.get(Constants.TRACE_ID));
        assertNull(meta.get(Constants.SPAN_ID));
        assertNull(meta.get(Constants.SAMPLED));
        assertNull(meta.get(Constants.CORRELATION_ID));
        assertNull(meta.get(Constants.TENANT_ID));
        assertNull(meta.get(Constants.ACTOR_ID));
        assertEquals(CALLER_CLASS, meta.get(Constants.CLAZZ));
        assertEquals(CALLER_METHOD, meta.get(Constants.METHOD));
        assertEquals(2, meta.size());
    }
}
