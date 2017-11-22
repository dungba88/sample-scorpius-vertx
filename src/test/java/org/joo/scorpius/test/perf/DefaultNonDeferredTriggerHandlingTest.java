package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.trigger.handle.DefaultHandlingStrategy;
import org.junit.Assert;

public class DefaultNonDeferredTriggerHandlingTest extends AbstractTriggerTest {
	
	private long processed = 0;
	
	public DefaultNonDeferredTriggerHandlingTest(long iterations) {
		super(iterations, new DefaultHandlingStrategy());
	}

	@Override
	protected void cleanup() {
		
	}

	@Override
	protected void doTest(long iterations, String msgName) {
		processed = 0;
		CountDownLatch latch = new CountDownLatch(1);
		
		for(int i=0; i<iterations; i++) {
			manager.fire(msgName, new SampleRequest("World"), response -> {
				if (++processed == iterations) {
					latch.countDown();
				}
			}, null);
		}
		
		try {
			latch.await(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue(processed == iterations);
	}
}
