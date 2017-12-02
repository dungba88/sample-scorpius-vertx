package org.joo.scorpius.support.queue;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class MPMCRingBuffer extends SPMCRingBuffer {

    public MPMCRingBuffer(final int maximumSize) {
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