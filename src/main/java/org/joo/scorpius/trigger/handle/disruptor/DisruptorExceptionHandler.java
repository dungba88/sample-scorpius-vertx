package org.joo.scorpius.trigger.handle.disruptor;

import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.trigger.TriggerExecutionContext;

import com.lmax.disruptor.ExceptionHandler;

public class DisruptorExceptionHandler implements ExceptionHandler {

	@Override
	public void handleEventException(Throwable ex, long sequence, Object event) {
		if (event == null || !(event instanceof ExecutionContextEvent))
			return;
		TriggerExecutionContext executionContext = ((ExecutionContextEvent)event).getExecutionContext();
		executionContext.fail(new TriggerExecutionException(ex));
	}

	@Override
	public void handleOnStartException(Throwable ex) {

	}

	@Override
	public void handleOnShutdownException(Throwable ex) {

	}
}
