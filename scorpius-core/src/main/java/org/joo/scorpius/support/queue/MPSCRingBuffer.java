package org.joo.scorpius.support.queue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class MPSCRingBuffer extends SPSCRingBuffer {

    protected AtomicBoolean lock = new AtomicBoolean(false);

    public MPSCRingBuffer(final int maximumSize) {
        super(maximumSize);
    }

    @Override
    public boolean enqueue(final TriggerExecutionContext executionContext) {
        while (!lock.compareAndSet(false, true)) {
        }
        try {
            return super.enqueue(executionContext);
        } finally {
            lock.set(false);
        }
    }
}