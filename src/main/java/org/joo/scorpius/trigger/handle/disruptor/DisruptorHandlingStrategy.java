package org.joo.scorpius.trigger.handle.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.joo.scorpius.support.builders.DefaultThreadFactoryBuilder;
import org.joo.scorpius.support.builders.contracts.TriggerThreadFactory;
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

    private TriggerThreadFactory threadFactory;

    private ProducerType producerType;

    public DisruptorHandlingStrategy() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public DisruptorHandlingStrategy(final int bufferSize) {
        this(bufferSize, new YieldingWaitStrategy());
    }

    public DisruptorHandlingStrategy(final int bufferSize, final WaitStrategy waitStrategy) {
        this(bufferSize, waitStrategy, ProducerType.MULTI);
    }

    public DisruptorHandlingStrategy(final int bufferSize, final WaitStrategy waitStrategy,
            final ProducerType producerType) {
        this(bufferSize, waitStrategy, producerType, new DefaultThreadFactoryBuilder().build());
    }

    @SuppressWarnings("unchecked")
    public DisruptorHandlingStrategy(final int bufferSize, final WaitStrategy waitStrategy,
            final ProducerType producerType, final TriggerThreadFactory threadFactory) {
        this.producerExecutor = Executors.newSingleThreadExecutor();
        this.producerType = producerType;
        this.threadFactory = threadFactory;
        this.disruptor = new Disruptor<>(new ExecutionContextEventFactory(), bufferSize, threadFactory, producerType,
                waitStrategy);
        this.disruptor.setDefaultExceptionHandler(new DisruptorExceptionHandler());
        this.disruptor.handleEventsWithWorkerPool(this::onEvent);
        this.disruptor.start();
    }

    @Override
    public void handle(final TriggerExecutionContext context) {
        boolean useAsync = producerType == ProducerType.MULTI && threadFactory.isConsumerThread(Thread.currentThread());
        Consumer<TriggerExecutionContext> handler = useAsync ? this::asyncPublishEvent : this::syncPublishEvent;
        handler.accept(context);
    }

    protected void asyncPublishEvent(final TriggerExecutionContext context) {
        if (producerExecutor.isShutdown()) {
            context.fail(new TriggerExecutionException("Executor has been shut down"));
            return;
        }
        producerExecutor.submit(() -> syncPublishEvent(context));
    }

    protected void syncPublishEvent(final TriggerExecutionContext context) {
        RingBuffer<ExecutionContextEvent> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            ExecutionContextEvent event = ringBuffer.get(sequence);
            event.setExecutionContext(context);
        } finally {
            ringBuffer.publish(sequence);
        }
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