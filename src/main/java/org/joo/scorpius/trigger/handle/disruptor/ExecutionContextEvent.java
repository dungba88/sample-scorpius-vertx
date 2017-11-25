package org.joo.scorpius.trigger.handle.disruptor;

import org.joo.scorpius.trigger.TriggerExecutionContext;

import lombok.Getter;
import lombok.Setter;

public class ExecutionContextEvent {

    private @Getter @Setter TriggerExecutionContext executionContext;
}
