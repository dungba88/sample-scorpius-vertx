package org.joo.scorpius.trigger.handle.disruptor;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class ExecutionContextEvent {

    private TriggerExecutionContext executionContext;

    public TriggerExecutionContext getExecutionContext() {
        return executionContext;
    }

    public void setExecutionContext(TriggerExecutionContext executionContext) {
        this.executionContext = executionContext;
    }
}
