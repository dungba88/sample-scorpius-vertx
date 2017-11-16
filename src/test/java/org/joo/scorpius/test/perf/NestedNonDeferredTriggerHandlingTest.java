package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.joo.scorpius.test.support.NestedRequest;
import org.joo.scorpius.test.support.NestedTrigger;
import org.junit.Assert;

public class NestedNonDeferredTriggerHandlingTest extends AbstractTriggerTest {
	
	private long processed = 0;
	
	public NestedNonDeferredTriggerHandlingTest(long iterations) {
		super(iterations);
	}
	
	protected void setup() {	
		super.setup();
		manager.registerTrigger("nested").withAction(NestedTrigger::new);
	}

	@Override
	protected void cleanup() {
		
	}

	@Override
	protected void doTest(long iterations, String msgName) {
		processed = 0;
		CountDownLatch latch = new CountDownLatch(1);
		
		for(int i=0; i<iterations; i++) {
			manager.fire("nested", new NestedRequest(manager.getApplicationContext().getIdGenerator().create(), msgName), response -> {
				if (++processed == iterations) {
					latch.countDown();
				}
			}, ex -> {
				ex.printStackTrace();
				if (++processed == iterations) {
					latch.countDown();
				}
			});
		}
		
		try {
			latch.await(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue(processed == iterations);
	}
}
