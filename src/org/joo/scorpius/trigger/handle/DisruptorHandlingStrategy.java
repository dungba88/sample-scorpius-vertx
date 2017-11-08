package org.joo.scorpius.trigger.handle;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.joo.scorpius.trigger.TriggerExecutionContext;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorHandlingStrategy implements TriggerHandlingStrategy, AutoCloseable {
	
	private Disruptor<ExecutionContextEvent> disruptor;
	
	public DisruptorHandlingStrategy() {
		this(1024, Executors.newCachedThreadPool());
	}

	@SuppressWarnings("unchecked")
	public DisruptorHandlingStrategy(int bufferSize, Executor executor) {
		this.disruptor = new Disruptor<>(new ExecutionContextEventFactory(), bufferSize, executor);
		this.disruptor.handleEventsWith(this::onEvent);
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
	
	private void onEvent(ExecutionContextEvent event, long sequence, boolean endOfBatch) {
		event.getExecutionContext().execute();
	}

	@Override
	public void close() throws Exception {
		disruptor.shutdown();
	}
}

class ExecutionContextEventFactory implements EventFactory<ExecutionContextEvent>
{
    public ExecutionContextEvent newInstance()
    {
        return new ExecutionContextEvent();
    }
}

class ExecutionContextEvent {

	private TriggerExecutionContext executionContext;
	
	public TriggerExecutionContext getExecutionContext() {
		return executionContext;
	}

	public void setExecutionContext(TriggerExecutionContext executionContext) {
		this.executionContext = executionContext;
	}
}
