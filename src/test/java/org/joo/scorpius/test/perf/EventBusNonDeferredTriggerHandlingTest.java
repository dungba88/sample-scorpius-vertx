package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.trigger.handle.vertx.EventBusHandlingStrategy;
import org.junit.Assert;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class EventBusNonDeferredTriggerHandlingTest extends AbstractTriggerTest {
	
	private EventBusHandlingStrategy strategy;
	
	private EventBus eventBus;
	
	private AtomicInteger processed = new AtomicInteger(0);
	
	public EventBusNonDeferredTriggerHandlingTest(long iterations) {
		super(iterations);
		Vertx vertx = Vertx.vertx();
		eventBus = vertx.eventBus();
		strategy = new EventBusHandlingStrategy(eventBus);
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
	protected void doTest(long iterations, String msgName) {
		processed = new AtomicInteger(0);
		CountDownLatch latch = new CountDownLatch(1);
		
		for(int i=0; i<iterations; i++) {
			manager.fire(msgName, new SampleRequest(), response -> {
				if (processed.incrementAndGet() == iterations) {
					latch.countDown();
				}
			}, null);
		}
		
		try {
			latch.await(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue(processed.get() == iterations);
	}
}