package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;

import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.trigger.handle.vertx.EventBusHandlingStrategy;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class EventBusNonDeferredTriggerHandlingTest extends AbstractTriggerTest {
	
	public static void main(String[] args) {
		EventBusNonDeferredTriggerHandlingTest testCase = new EventBusNonDeferredTriggerHandlingTest(10000000);
		testCase.test();
	}
	
	private EventBusHandlingStrategy strategy;
	
	private EventBus eventBus;
	
	private long processed = 0;
	
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