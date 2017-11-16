package org.joo.scorpius.support.queue;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class SPSCRingBuffer implements HandlingQueue {

	private int mask;

	private TriggerExecutionContext[] data;

	private volatile int head;

	private volatile int tail;

	private static final long headOffset;

	private static final long tailOffset;

	private static final int dataBaseOffset;

	private static final int indexScale;

	static {
		try {
			headOffset = UnsafeUtils.objectFieldOffset(SPSCRingBuffer.class.getDeclaredField("head"));
			tailOffset = UnsafeUtils.objectFieldOffset(SPSCRingBuffer.class.getDeclaredField("tail"));
			dataBaseOffset = UnsafeUtils.arrayBaseOffset(TriggerExecutionContext[].class);
			indexScale = UnsafeUtils.arrayIndexScale(TriggerExecutionContext[].class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public SPSCRingBuffer(int maximumSize) {
		if (!isPowerOf2(maximumSize)) {
			throw new RuntimeException("Maximum size must be power of 2");
		}
		data = new TriggerExecutionContext[maximumSize];
		mask = maximumSize - 1;
		head = tail = 0;
	}

	private boolean isPowerOf2(int maximumSize) {
		return (maximumSize & (maximumSize - 1)) == 0;
	}

	@Override
	public boolean enqueue(TriggerExecutionContext context) {
		int nextTail = (tail + indexScale) & mask;
		if (nextTail == head)
			return false;
		UnsafeUtils.putObject(data, dataBaseOffset + tail, context);
		UnsafeUtils.putOrderedInt(this, tailOffset, nextTail);
		return true;
	}

	@Override
	public TriggerExecutionContext dequeue() {
		if (isEmpty())
			return null;
		TriggerExecutionContext result = (TriggerExecutionContext) UnsafeUtils.getObject(data, dataBaseOffset + head);
		UnsafeUtils.putOrderedInt(this, headOffset, (head + indexScale) & mask);
		return result;
	}

	public boolean isEmpty() {
		return head == tail;
	}
}