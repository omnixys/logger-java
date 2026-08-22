package com.omnixys.logger.logging;

import com.omnixys.logger.model.LogDTO;
import com.omnixys.logger.model.LogLevel;
import com.omnixys.logger.transport.LogTransport;
import com.omnixys.observability.api.TraceContext;
import com.omnixys.observability.api.TraceContextSnapshot;
import com.omnixys.observability.api.TracePropagation;
import com.omnixys.observability.api.TraceScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncBatchLoggerTest {

    @Mock
    private LogTransport transport;

    @Mock
    private TracePropagation<?> tracing;

    @Mock
    private TraceContextSnapshot snapshot;

    @Mock
    private TraceScope scope;

    private final TraceContext ctx = new TraceContext("trace-1", "span-1");
    private final LogDTO log = new LogDTO("svc", LogLevel.INFO, "msg", Instant.now(), null, null, null);

    @BeforeEach
    void setUp() {
        lenient().when(tracing.capture()).thenReturn(snapshot);
        lenient().when(tracing.currentContext()).thenReturn(ctx);
        lenient().when(snapshot.activate()).thenReturn(scope);
    }

    @Test
    void submit_shouldBufferUntilShutdownFlushes() {
        AsyncBatchLogger batch = new AsyncBatchLogger(transport, tracing, 10, 60_000);

        batch.submit(log);

        verify(transport, never()).send(any(), any());

        batch.shutdown();

        verify(transport).send(log, ctx);
    }

    @Test
    void submit_shouldSendDirectlyWhenQueueIsFull() {
        AsyncBatchLogger batch = new AsyncBatchLogger(transport, tracing, 1, 60_000);

        batch.submit(log);
        batch.submit(log);

        verify(transport).send(log, ctx);

        batch.shutdown();

        verify(transport, times(2)).send(log, ctx);
    }

    @Test
    void shutdown_shouldBeSafeToCallMultipleTimes() {
        AsyncBatchLogger batch = new AsyncBatchLogger(transport, tracing, 10, 60_000);

        batch.shutdown();
        batch.shutdown();

        verify(transport, never()).send(any(), any());
    }
}
