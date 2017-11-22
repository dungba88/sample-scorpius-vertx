package org.joo.scorpius.trigger.handle;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public interface TriggerHandlingStrategy extends AutoCloseable {

	public void handle(TriggerExecutionContext context);
}
