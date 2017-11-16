package org.joo.scorpius.trigger.handle.disruptor;

import com.lmax.disruptor.ExceptionHandler;

public class DisruptorExceptionHandler implements ExceptionHandler {
	
	@Override
	public void handleEventException(Throwable ex, long sequence, Object event) {
		if (event == null || !(event instanceof ExecutionContextEvent))
			return;

		//TODO log
	}

	@Override
	public void handleOnStartException(Throwable ex) {

	}

	@Override
	public void handleOnShutdownException(Throwable ex) {

	}
}
