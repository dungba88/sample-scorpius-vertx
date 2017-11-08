package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;

import org.joo.scorpius.support.queue.SPMCRingBuffer;
import org.joo.scorpius.support.queue.UnsafeSPSCRingBuffer;
import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.trigger.handle.QueueHandlingStrategy;

public class QueueTriggerHandlingTest extends AbstractTriggerTest {
	
	public static void main(String[] args) {
		QueueTriggerHandlingTest testCase = new QueueTriggerHandlingTest(10000000);
		testCase.test();
	}
	
	private long processed = 0;
	
	private QueueHandlingStrategy strategy;
	
	public QueueTriggerHandlingTest(long iterations) {
		super(iterations);
		strategy = new QueueHandlingStrategy(new UnsafeSPSCRingBuffer(1024 * 1024 * 16), 1);
	}

	@Override
	protected void doTest() {
		processed = 0;
		CountDownLatch latch = new CountDownLatch(1);
		
		for(int i=0; i<iterations; i++) {
			manager.fire("greet", new SampleRequest(), strategy).done(response -> {
				if (++processed == iterations) {
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
	protected void warmup() {
		manager.fire("greet", new SampleRequest());
	}
}
