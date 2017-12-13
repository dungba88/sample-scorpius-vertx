package org.joo.scorpius.trigger.handle;

import org.joo.scorpius.support.queue.HandlingQueue;
import org.joo.scorpius.trigger.TriggerExecutionContext;

public class QueueHandlingStrategy extends AbstractTriggerHandlingStrategy {

    private HandlingQueue queue;

    private ConsumerThread[] consumerThreads;

    public QueueHandlingStrategy(final HandlingQueue queue, final int noConsumers) {
        this.queue = queue;
        this.consumerThreads = new ConsumerThread[noConsumers];
        for (int i = 0; i < noConsumers; i++) {
            this.consumerThreads[i] = new ConsumerThread(queue);
        }
    }

    @Override
    public void handle(final TriggerExecutionContext context) {
        while (!queue.enqueue(context)) {
            // Do nothing
        }
    }

    @Override
    protected void doStart() {
        for (ConsumerThread thread : consumerThreads) {
            thread.start();
        }
    }

    @Override
    protected void doShutdown() {
        for (ConsumerThread thread : consumerThreads) {
            thread.cancel();
        }
        for (ConsumerThread thread : consumerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                thread.interrupt();
            }
        }
    }
}

class ConsumerThread extends Thread {

    private HandlingQueue queue;

    public ConsumerThread(final HandlingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            while (!Thread.currentThread().isInterrupted() && queue.isEmpty()) {
                // Thread.onSpinWait();
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