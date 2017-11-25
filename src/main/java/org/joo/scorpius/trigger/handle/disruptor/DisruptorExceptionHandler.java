package org.joo.scorpius.trigger.handle.disruptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lmax.disruptor.ExceptionHandler;

public class DisruptorExceptionHandler implements ExceptionHandler {

    private final static Logger logger = LogManager.getLogger(DisruptorExceptionHandler.class);

    @Override
    public void handleEventException(final Throwable ex, final long sequence, final Object event) {
        if (event == null || !(event instanceof ExecutionContextEvent))
            return;

        logger.error("Exception on disruptor worker pool", ex);
    }

    @Override
    public void handleOnStartException(final Throwable ex) {
        // doesn't care
    }

    @Override
    public void handleOnShutdownException(final Throwable ex) {
        // doesn't care
    }
}
