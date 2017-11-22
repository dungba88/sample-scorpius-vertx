package org.joo.scorpius.trigger.handle;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class DefaultHandlingStrategy implements TriggerHandlingStrategy {

	@Override
	public void handle(TriggerExecutionContext context) {
		context.execute();
	}

	@Override
	public void close() throws Exception {
		
	}
}
