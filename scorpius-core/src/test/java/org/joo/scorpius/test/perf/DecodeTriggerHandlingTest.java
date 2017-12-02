package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.joo.scorpius.support.exception.MalformedRequestException;
import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.trigger.handle.DefaultHandlingStrategy;
import org.junit.Assert;

public class DecodeTriggerHandlingTest extends AbstractTriggerTest {
	
	private long processed = 0;
	
	private String json;
	
	public DecodeTriggerHandlingTest(long iterations) {
		super(iterations, new DefaultHandlingStrategy());
		json = "{\"name\": \"World\"}";
	}

	@Override
	protected void doTest(long iterations, String msgName) {
		processed = 0;
		CountDownLatch latch = new CountDownLatch(1);
		
		for(int i=0; i<iterations; i++) {
		    SampleRequest request;
            try {
                request = (SampleRequest) manager.decodeRequestForEvent(msgName, json);
            } catch (MalformedRequestException e) {
                Assert.fail(e.getMessage());
                return;
            }
			manager.fire(msgName, request, response -> {
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
