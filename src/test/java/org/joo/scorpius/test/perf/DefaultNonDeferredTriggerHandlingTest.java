package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;

import org.joo.scorpius.test.support.SampleRequest;

public class DefaultNonDeferredTriggerHandlingTest extends AbstractTriggerTest {
	
	public static void main(String[] args) {
		DefaultNonDeferredTriggerHandlingTest testCase = new DefaultNonDeferredTriggerHandlingTest(10000000);
		testCase.test();
	}
	
	private long processed = 0;
	
	public DefaultNonDeferredTriggerHandlingTest(long iterations) {
		super(iterations);
	}

	@Override
	protected void cleanup() {
		
	}

	@Override
	protected void doTest(String msgName) {
		processed = 0;
		CountDownLatch latch = new CountDownLatch(1);
		
		for(int i=0; i<iterations; i++) {
			manager.fire(msgName, new SampleRequest(), response -> {
				if (++processed == iterations) {
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
}
