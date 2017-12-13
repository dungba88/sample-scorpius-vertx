package org.joo.scorpius.trigger.handle;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractTriggerHandlingStrategy implements TriggerHandlingStrategy {
    
    private AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void start() {
        if (started.compareAndSet(false, true))
            doStart();
    }

    @Override
    public void shutdown() {
        if (started.compareAndSet(true, false))
            doShutdown();
    }

    protected abstract void doStart();

    protected abstract void doShutdown();

    public boolean isStarted() {
        return started.get();
    }
}
