package org.joo.scorpius.trigger.handle;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class DefaultHandlingStrategy implements TriggerHandlingStrategy {

    @Override
    public void handle(final TriggerExecutionContext context) {
        context.execute();
    }

    @Override
    public void start() {
        // do nothing
    }

    @Override
    public void shutdown() {
        // do nothing
    }
}
