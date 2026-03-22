package com.omnixys.logger.logging;

import com.omnixys.logger.model.ContextAwareLog;
import com.omnixys.logger.model.LogDTO;
import com.omnixys.logger.transport.LogTransport;
import io.opentelemetry.context.Context;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Non-blocking async batch logger.
 */
public class AsyncBatchLogger {

    private final LogTransport transport;
    private final BlockingQueue<ContextAwareLog> queue;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public AsyncBatchLogger(LogTransport transport, int capacity, long flushIntervalMs) {
        this.transport = transport;
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
        Context context = Context.current();

        queue.offer(new ContextAwareLog(context, log));
    }

    private void flush() {
        while (!queue.isEmpty()) {
            ContextAwareLog entry = queue.poll();

            if (entry != null) {
                try (var scope = entry.context().makeCurrent()) {
                    transport.send(entry.log());
                }
            }
        }
    }

    public void shutdown() {
        running.set(false);
        scheduler.shutdown();
        flush();
    }
}