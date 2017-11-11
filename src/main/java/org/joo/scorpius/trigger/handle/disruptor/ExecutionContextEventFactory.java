package org.joo.scorpius.trigger.handle.disruptor;

import com.lmax.disruptor.EventFactory;

public class ExecutionContextEventFactory implements EventFactory<ExecutionContextEvent>
{
    public ExecutionContextEvent newInstance()
    {
        return new ExecutionContextEvent();
    }
}