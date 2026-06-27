package com.omnixys.logger.logging;

import com.omnixys.logger.model.ContextAwareLog;
import com.omnixys.logger.model.LogDTO;
import com.omnixys.logger.transport.LogTransport;
import com.omnixys.observability.api.TraceContext;
import com.omnixys.observability.api.TraceContextSnapshot;
import com.omnixys.observability.api.TracePropagation;
import io.opentelemetry.context.Context;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Non-blocking async batch logger.
 */
@Deprecated(forRemoval = true)
public class AsyncBatchLogger {

    private final LogTransport transport;
    private final BlockingQueue<ContextAwareLog> queue;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final TracePropagation<?> tracing;

    public AsyncBatchLogger(
            LogTransport transport,
            TracePropagation<?> tracing,
            int capacity,
            long flushIntervalMs
    ) {
        this.transport = transport;
        this.tracing = tracing;
        this.queue = new ArrayBlockingQueue<>(capacity);

        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("omnixys-logger-batch");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(this::flush, flushIntervalMs, flushIntervalMs, TimeUnit.MILLISECONDS);
    }

    public void submit(LogDTO log) {

        TraceContextSnapshot snapshot = tracing.capture();
        TraceContext traceContext = tracing.currentContext();

        boolean accepted = queue.offer(new ContextAwareLog(snapshot, log, traceContext));
        if (!accepted) {
            transport.send(log, traceContext);
        }
    }

    private void flush() {
        try {
            while (!queue.isEmpty()) {
                ContextAwareLog entry = queue.poll();

                if (entry != null) {
                    try (var scope = entry.snapshot().activate()) {
                        transport.send(entry.log(), entry.traceContext());
                    }
                }
            }
        } catch (Exception e) {
            Thread currentThread = Thread.currentThread();
            currentThread.getUncaughtExceptionHandler().uncaughtException(currentThread, e);
        }
    }

    public void shutdown() {
        running.set(false);
        scheduler.shutdown();
        flush();
    }
}