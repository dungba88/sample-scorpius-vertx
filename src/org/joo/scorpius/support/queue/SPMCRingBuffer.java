package org.joo.scorpius.support.queue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class SPMCRingBuffer implements HandlingQueue {
	
	private int mask;

	private TriggerExecutionContext[] data;
	
	private volatile int head;
	
	private volatile int tail;
	
	private AtomicBoolean lock = new AtomicBoolean(false);
	
	private static final long headOffset;

	private static final long tailOffset;

	private static final int dataBaseOffset;

	private static final int indexScale;
	
	static {
		try {
			headOffset = UnsafeUtils.objectFieldOffset(SPMCRingBuffer.class.getDeclaredField("head"));
			tailOffset = UnsafeUtils.objectFieldOffset(SPMCRingBuffer.class.getDeclaredField("tail"));
			dataBaseOffset = UnsafeUtils.arrayBaseOffset(TriggerExecutionContext[].class);
			indexScale = UnsafeUtils.arrayIndexScale(TriggerExecutionContext[].class);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
	
	public SPMCRingBuffer(int maximumSize) {
		if (!isPowerOf2(maximumSize)) {
			throw new RuntimeException("Maximum size must be power of 2");
		}
		data = new TriggerExecutionContext[maximumSize];
		head = tail = 0;
		mask = maximumSize - 1;
	}
	
	private boolean isPowerOf2(int maximumSize) {
		return (maximumSize & (maximumSize - 1)) == 0;
	}

	@Override
	public boolean enqueue(TriggerExecutionContext number) {
		int nextTail = (tail + indexScale) & mask;
		if (nextTail == head) return false;
		UnsafeUtils.putObject(data, dataBaseOffset + tail, number);
		UnsafeUtils.putOrderedInt(this, tailOffset, nextTail);
		return true;
	}

	@Override
	public TriggerExecutionContext dequeue() {
		while(!lock.compareAndSet(false, true)) {}
		try {
			if (isEmpty()) return null;
			TriggerExecutionContext result = (TriggerExecutionContext) UnsafeUtils.getObject(data, dataBaseOffset + head);
			UnsafeUtils.putOrderedInt(this, headOffset, (head + indexScale) & mask);
			return result;
		} finally {
			lock.set(false);
		}
	}
	
	public boolean isEmpty() {
		return head == tail;
	}
}