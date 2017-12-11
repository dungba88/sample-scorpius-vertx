package org.joo.scorpius.trigger.handle;

import org.joo.scorpius.support.LifeCycle;
import org.joo.scorpius.trigger.TriggerExecutionContext;

public interface TriggerHandlingStrategy extends LifeCycle {

    public void handle(final TriggerExecutionContext context);
}
