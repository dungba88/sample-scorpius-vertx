package org.joo.scorpius.trigger.handle.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.joo.scorpius.support.builders.DefaultThreadFactoryBuilder;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class DisruptorHandlingStrategy implements TriggerHandlingStrategy, AutoCloseable {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private ExecutorService producerExecutor;

    private Disruptor<ExecutionContextEvent> disruptor;

    public DisruptorHandlingStrategy() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public DisruptorHandlingStrategy(final int bufferSize) {
        this(bufferSize, ProducerType.SINGLE, new YieldingWaitStrategy());
    }

    public DisruptorHandlingStrategy(final int bufferSize, final ProducerType producerType,
            final WaitStrategy waitStrategy) {
        this(bufferSize, producerType, waitStrategy, new DefaultThreadFactoryBuilder().build());
    }

    @SuppressWarnings("unchecked")
    public DisruptorHandlingStrategy(final int bufferSize, final ProducerType producerType,
            final WaitStrategy waitStrategy, final ThreadFactory threadFactory) {
        this.producerExecutor = Executors.newSingleThreadExecutor();
        this.disruptor = new Disruptor<>(new ExecutionContextEventFactory(), bufferSize, threadFactory, producerType,
                waitStrategy);
        this.disruptor.setDefaultExceptionHandler(new DisruptorExceptionHandler());
        this.disruptor.handleEventsWithWorkerPool(this::onEvent);
        this.disruptor.start();
    }

    @Override
    public void handle(final TriggerExecutionContext context) {
        if (producerExecutor.isShutdown()) {
            context.fail(new TriggerExecutionException("Executor has been shut down"));
            return;
        }
        producerExecutor.submit(() -> {
            RingBuffer<ExecutionContextEvent> ringBuffer = disruptor.getRingBuffer();
            long sequence = ringBuffer.next();
            try {
                ExecutionContextEvent event = ringBuffer.get(sequence);
                event.setExecutionContext(context);
            } finally {
                ringBuffer.publish(sequence);
            }
        });
    }

    private void onEvent(final ExecutionContextEvent event) throws Exception {
        event.getExecutionContext().execute();
    }

    @Override
    public void close() throws Exception {
        disruptor.shutdown();
        producerExecutor.shutdown();
    }
}