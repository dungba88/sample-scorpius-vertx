package org.joo.scorpius.trigger.handle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class ExecutorHandlingStrategy implements TriggerHandlingStrategy, AutoCloseable {

    private boolean ownedExecutor;

    private ExecutorService executor;

    public ExecutorHandlingStrategy(int noThreads) {
        this.executor = Executors.newFixedThreadPool(noThreads);
        this.ownedExecutor = true;
    }

    public ExecutorHandlingStrategy(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void handle(TriggerExecutionContext context) {
        executor.submit(() -> context.execute());
    }

    @Override
    public void close() throws Exception {
        if (ownedExecutor) {
            executor.shutdown();
        }
    }
}