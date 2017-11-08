package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.joo.scorpius.support.queue.UnsafeSPSCRingBuffer;
import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.trigger.handle.QueueHandlingStrategy;

public class SingleConsumerQueueTriggerHandlingTest extends AbstractTriggerTest {
	
	public static void main(String[] args) {
		SingleConsumerQueueTriggerHandlingTest testCase = new SingleConsumerQueueTriggerHandlingTest(10000000);
		testCase.test();
	}
	
	private AtomicInteger processed = new AtomicInteger(0);
	
	private QueueHandlingStrategy strategy;
	
	public SingleConsumerQueueTriggerHandlingTest(long iterations) {
		super(iterations);
		strategy = new QueueHandlingStrategy(new UnsafeSPSCRingBuffer(1024 * 1024 * 16), 1);
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
			manager.fire("greet", new SampleRequest(), strategy).done(response -> {
				if (processed.incrementAndGet() == iterations) {
					latch.countDown();
				}
			});
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		strategy.stop();
	}

	@Override
	protected void cleanup() {
		
	}
}
