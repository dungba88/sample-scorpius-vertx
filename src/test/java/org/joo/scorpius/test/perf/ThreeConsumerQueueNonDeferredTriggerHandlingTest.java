package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.joo.scorpius.support.queue.SPMCRingBuffer;
import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.trigger.handle.QueueHandlingStrategy;

public class ThreeConsumerQueueNonDeferredTriggerHandlingTest extends AbstractTriggerTest {
	
	public static void main(String[] args) {
		ThreeConsumerQueueNonDeferredTriggerHandlingTest testCase = new ThreeConsumerQueueNonDeferredTriggerHandlingTest(10000000);
		testCase.test();
	}
	
	private AtomicInteger processed = new AtomicInteger(0);
	
	private QueueHandlingStrategy strategy;
	
	public ThreeConsumerQueueNonDeferredTriggerHandlingTest(long iterations) {
		super(iterations);
		strategy = new QueueHandlingStrategy(new SPMCRingBuffer(1024 * 1024 * 16), 3);
		manager.setHandlingStrategy(strategy);
	}

	@Override
	protected void warmup() {
		manager.fire("greet", new SampleRequest());
	}

	@Override
	protected void doTest() {
		processed = new AtomicInteger(0);
		CountDownLatch latch = new CountDownLatch(1);
		
		for(int i=0; i<iterations; i++) {
			manager.fire("greet", new SampleRequest(), response -> {
				if (processed.incrementAndGet() == iterations) {
					latch.countDown();
				}
			}, null);
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
