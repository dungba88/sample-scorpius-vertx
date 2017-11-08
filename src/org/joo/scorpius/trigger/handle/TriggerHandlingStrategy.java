package org.joo.scorpius.trigger.handle;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public interface TriggerHandlingStrategy {

	public void handle(TriggerExecutionContext context);
}
