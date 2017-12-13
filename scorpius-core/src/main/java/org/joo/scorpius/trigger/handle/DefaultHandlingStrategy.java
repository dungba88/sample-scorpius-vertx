package org.joo.scorpius.trigger.handle;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class DefaultHandlingStrategy extends AbstractTriggerHandlingStrategy {

    @Override
    public void handle(final TriggerExecutionContext context) {
        context.execute();
    }

    @Override
    protected void doStart() {
        // do nothing
    }

    @Override
    protected void doShutdown() {
        // do nothing
    }
}
