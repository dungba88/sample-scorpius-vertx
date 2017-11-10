package org.joo.scorpius.support.queue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public class SPMCRingBuffer extends SPSCRingBuffer {
	
	private AtomicBoolean lock = new AtomicBoolean(false);
	
	public SPMCRingBuffer(int maximumSize) {
		super(maximumSize);
	}
	
	@Override
	public TriggerExecutionContext dequeue() {
		while(!lock.compareAndSet(false, true)) {}
		try {
			return super.dequeue();
		} finally {
			lock.set(false);
		}
	}
}