package org.joo.scorpius.support.queue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class SPMCRingBuffer extends SPSCRingBuffer {

    protected AtomicBoolean lock = new AtomicBoolean(false);

    public SPMCRingBuffer(int maximumSize) {
        super(maximumSize);
    }

    @Override
    public TriggerExecutionContext dequeue() {
        if (isEmpty())
            return null;
        while (!lock.compareAndSet(false, true)) {
        }
        try {
            return super.dequeue();
        } finally {
            lock.set(false);
        }
    }
}