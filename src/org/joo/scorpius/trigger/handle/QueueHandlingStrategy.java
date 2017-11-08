package org.joo.scorpius.trigger.handle;

import org.joo.scorpius.support.queue.HandlingQueue;
import org.joo.scorpius.trigger.TriggerExecutionContext;

public class QueueHandlingStrategy implements TriggerHandlingStrategy {
	
	private HandlingQueue queue;
	
	private ConsumerThread[] consumerThreads;

	public QueueHandlingStrategy(HandlingQueue queue, int noConsumers) {
		this.queue = queue;
		this.consumerThreads = new ConsumerThread[noConsumers];
		for(int i=0; i < noConsumers; i++) {
			this.consumerThreads[i] = new ConsumerThread(queue);
		}
		for(int i=0; i < noConsumers; i++) {
			this.consumerThreads[i].start();
		}
	}
	
	public void stop() {
		for(ConsumerThread thread: consumerThreads) {
			thread.cancel();
		}
	}

	@Override
	public void handle(TriggerExecutionContext context) {
		queue.enqueue(context);
	}
}

class ConsumerThread extends Thread {
	
	private HandlingQueue queue;

	public ConsumerThread(HandlingQueue queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			while(!Thread.currentThread().isInterrupted() && queue.isEmpty()) {
				Thread.onSpinWait();
			}
			TriggerExecutionContext context = queue.dequeue();
			if (context != null) {
				context.execute();
			}
		}
	}
	
	public void cancel() {
		interrupt();
	}
}