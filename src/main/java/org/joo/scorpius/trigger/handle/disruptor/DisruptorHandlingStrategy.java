package org.joo.scorpius.trigger.handle.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class DisruptorHandlingStrategy implements TriggerHandlingStrategy, AutoCloseable {

    private final static int DEFAULT_BUFFER_SIZE = 1024;
    
    private ExecutorService producerExecutor;

    private ExecutorService executor;

    private Disruptor<ExecutionContextEvent> disruptor;

    public DisruptorHandlingStrategy() {
        this(DEFAULT_BUFFER_SIZE, Executors.newCachedThreadPool());
    }

    public DisruptorHandlingStrategy(final int bufferSize, final ExecutorService executor) {
        this(bufferSize, executor, ProducerType.SINGLE, new YieldingWaitStrategy());
    }

    @SuppressWarnings("unchecked")
    public DisruptorHandlingStrategy(final int bufferSize, final ExecutorService executor,
            final ProducerType producerType, final WaitStrategy waitStategy) {
        this.producerExecutor = Executors.newFixedThreadPool(1);
        this.executor = executor;
        this.disruptor = new Disruptor<>(new ExecutionContextEventFactory(), bufferSize, executor, producerType,
                waitStategy);
        this.disruptor.handleExceptionsWith(new DisruptorExceptionHandler());
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
        executor.shutdown();
        producerExecutor.shutdown();
    }
}