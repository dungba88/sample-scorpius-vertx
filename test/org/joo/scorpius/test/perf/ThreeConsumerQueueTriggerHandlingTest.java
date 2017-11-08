package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.joo.scorpius.support.queue.SPMCRingBuffer;
import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.trigger.handle.QueueHandlingStrategy;

public class ThreeConsumerQueueTriggerHandlingTest extends AbstractTriggerTest {
	
	public static void main(String[] args) {
		ThreeConsumerQueueTriggerHandlingTest testCase = new ThreeConsumerQueueTriggerHandlingTest(10000000);
		testCase.test();
	}
	
	private AtomicInteger processed = new AtomicInteger(0);
	
	private QueueHandlingStrategy strategy;
	
	public ThreeConsumerQueueTriggerHandlingTest(long iterations) {
		super(iterations);
		strategy = new QueueHandlingStrategy(new SPMCRingBuffer(1024 * 1024 * 16), 3);
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
			latch.await(7000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(processed.get());
	}
	
	@Override
	protected void cleanup() {
		strategy.stop();
	}
}
