package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;

import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.trigger.handle.DisruptorHandlingStrategy;

public class DisruptorTriggerHandlingTest extends AbstractTriggerTest {
	
	public static void main(String[] args) {
		DisruptorTriggerHandlingTest testCase = new DisruptorTriggerHandlingTest(10000000);
		testCase.test();
	}
	
	private DisruptorHandlingStrategy strategy;
	
	private long processed = 0;
	
	public DisruptorTriggerHandlingTest(long iterations) {
		super(iterations);
		strategy = new DisruptorHandlingStrategy();
		manager.setHandlingStrategy(strategy);
	}

	@Override
	protected void cleanup() {
		try {
			strategy.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doTest() {
		processed = 0;
		CountDownLatch latch = new CountDownLatch(1);
		
		for(int i=0; i<iterations; i++) {
			manager.fire("greet", new SampleRequest()).done(response -> {
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
	}

	@Override
	protected void warmup() {
		manager.fire("greet", new SampleRequest());
	}
}
