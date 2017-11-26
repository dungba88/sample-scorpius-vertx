package org.joo.scorpius.support.queue;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class SPSCRingBuffer implements HandlingQueue {

    private int mask;

    private TriggerExecutionContext[] data;

    private volatile int head;

    private volatile int tail;

    private static final long HEAD_OFFSET;

    private static final long TAIL_OFFSET;

    private static final int DATA_BASE_OFFSET;

    private static final int INDEX_SCALE;

    static {
        try {
            HEAD_OFFSET = UnsafeUtils.objectFieldOffset(SPSCRingBuffer.class.getDeclaredField("head"));
            TAIL_OFFSET = UnsafeUtils.objectFieldOffset(SPSCRingBuffer.class.getDeclaredField("tail"));
            DATA_BASE_OFFSET = UnsafeUtils.arrayBaseOffset(TriggerExecutionContext[].class);
            INDEX_SCALE = UnsafeUtils.arrayIndexScale(TriggerExecutionContext[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SPSCRingBuffer(final int maximumSize) {
        if (!isPowerOf2(maximumSize)) {
            throw new IllegalArgumentException("Maximum size must be power of 2");
        }
        data = new TriggerExecutionContext[maximumSize];
        mask = maximumSize - 1;
        head = tail = 0;
    }

    private boolean isPowerOf2(final int maximumSize) {
        return (maximumSize & (maximumSize - 1)) == 0;
    }

    @Override
    public boolean enqueue(final TriggerExecutionContext context) {
        int nextTail = (tail + INDEX_SCALE) & mask;
        if (nextTail == head)
            return false;
        UnsafeUtils.putObject(data, DATA_BASE_OFFSET + tail, context);
        UnsafeUtils.putOrderedInt(this, TAIL_OFFSET, nextTail);
        return true;
    }

    @Override
    public TriggerExecutionContext dequeue() {
        if (isEmpty())
            return null;
        TriggerExecutionContext result = (TriggerExecutionContext) UnsafeUtils.getObject(data, DATA_BASE_OFFSET + head);
        UnsafeUtils.putOrderedInt(this, HEAD_OFFSET, (head + INDEX_SCALE) & mask);
        return result;
    }

    public boolean isEmpty() {
        return head == tail;
    }
}