package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.joo.scorpius.support.queue.SPSCRingBuffer;
import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.trigger.handle.QueueHandlingStrategy;
import org.junit.Assert;

public class SingleConsumerQueueNonDeferredTriggerHandlingTest extends AbstractTriggerTest {
	
	private AtomicInteger processed = new AtomicInteger(0);
	
	private QueueHandlingStrategy strategy;
	
	public SingleConsumerQueueNonDeferredTriggerHandlingTest(long iterations) {
		super(iterations);
		strategy = new QueueHandlingStrategy(new SPSCRingBuffer(1024 * 1024 * 16), 1);
		manager.setHandlingStrategy(strategy);
	}

	@Override
	protected void doTest(long iterations, String msgName) {
		processed = new AtomicInteger(0);
		CountDownLatch latch = new CountDownLatch(1);
		
		for(int i=0; i<iterations; i++) {
			manager.fire(msgName, new SampleRequest(), response -> {
				if (processed.incrementAndGet() == iterations) {
					latch.countDown();
				}
			}, null);
		}
		
		try {
			latch.await(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue(processed.get() == iterations);
	}

	@Override
	protected void cleanup() {
		try {
			strategy.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
