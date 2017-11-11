package org.joo.scorpius.trigger.handle;

import org.joo.scorpius.support.queue.HandlingQueue;
import org.joo.scorpius.trigger.TriggerExecutionContext;

public class QueueHandlingStrategy implements TriggerHandlingStrategy, AutoCloseable {
	
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
	
	@Override
	public void handle(TriggerExecutionContext context) {
		queue.enqueue(context);
	}

	@Override
	public void close() throws Exception {
		for(ConsumerThread thread: consumerThreads) {
			thread.cancel();
		}
		for(ConsumerThread thread: consumerThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {}
		}
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