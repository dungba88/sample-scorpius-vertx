package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;

import org.joo.scorpius.test.support.SampleRequest;

public class DefaultTriggerHandlingTest extends AbstractTriggerTest {
	
	public static void main(String[] args) {
		DefaultTriggerHandlingTest testCase = new DefaultTriggerHandlingTest(10000000);
		testCase.test();
	}
	
	private long processed = 0;
	
	public DefaultTriggerHandlingTest(long iterations) {
		super(iterations);
	}

	@Override
	protected void cleanup() {
		
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
