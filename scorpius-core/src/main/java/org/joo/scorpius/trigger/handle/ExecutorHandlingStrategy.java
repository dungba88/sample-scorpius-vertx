package org.joo.scorpius.trigger.handle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class ExecutorHandlingStrategy extends AbstractTriggerHandlingStrategy {

    private boolean ownedExecutor;

    private ExecutorService executor;

    public ExecutorHandlingStrategy(final int noThreads) {
        this.executor = Executors.newFixedThreadPool(noThreads);
        this.ownedExecutor = true;
    }

    public ExecutorHandlingStrategy(final ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void handle(final TriggerExecutionContext context) {
        executor.submit(context::execute);
    }

    @Override
    protected void doStart() {
        
    }

    @Override
    protected void doShutdown() {
        if (ownedExecutor)
            executor.shutdown();
    }
}