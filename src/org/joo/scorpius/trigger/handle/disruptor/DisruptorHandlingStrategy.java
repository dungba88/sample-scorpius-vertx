package org.joo.scorpius.trigger.handle.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class DisruptorHandlingStrategy implements TriggerHandlingStrategy, AutoCloseable {
	
	private ExecutorService executor;
	
	private Disruptor<ExecutionContextEvent> disruptor;
	
	public DisruptorHandlingStrategy() {
		this(1024, Executors.newCachedThreadPool());
	}

	@SuppressWarnings("unchecked")
	public DisruptorHandlingStrategy(int bufferSize, ExecutorService executor) {
		this.executor = executor;
		this.disruptor = new Disruptor<>(new ExecutionContextEventFactory(), bufferSize, executor);
		this.disruptor.handleEventsWith(this::onEvent);
		this.disruptor.start();
	}
	
	public DisruptorHandlingStrategy(int bufferSize, ExecutorService executor, ProducerType producerType, WaitStrategy waitStrategy) {
		this(bufferSize, executor, producerType, waitStrategy, false);
	}
	
	@SuppressWarnings("unchecked")
	public DisruptorHandlingStrategy(int bufferSize, ExecutorService executor, ProducerType producerType, WaitStrategy waitStategy, boolean workerPool) {
		this.executor = executor;
		this.disruptor = new Disruptor<>(new ExecutionContextEventFactory(), bufferSize, executor, producerType, waitStategy);
		if (workerPool) {
			this.disruptor.handleEventsWithWorkerPool(this::onEvent);
		} else {
			this.disruptor.handleEventsWith(this::onEvent);
		}
		this.disruptor.handleExceptionsWith(new DisruptorExceptionHandler());
		this.disruptor.start();
	}
	
	@Override
	public void handle(TriggerExecutionContext context) {
		RingBuffer<ExecutionContextEvent> ringBuffer = disruptor.getRingBuffer(); 
		long sequence = ringBuffer.next();
        try
        {
        	ExecutionContextEvent event = ringBuffer.get(sequence);
            event.setExecutionContext(context);
        }
        finally
        {
            ringBuffer.publish(sequence);
        }
	}
	
	private void onEvent(ExecutionContextEvent event) throws Exception {
		event.getExecutionContext().execute();
	}
	
	private void onEvent(ExecutionContextEvent event, long sequence, boolean endOfBatch) {
		event.getExecutionContext().execute();
	}

	@Override
	public void close() throws Exception {
		disruptor.shutdown();
		executor.shutdown();
	}
}