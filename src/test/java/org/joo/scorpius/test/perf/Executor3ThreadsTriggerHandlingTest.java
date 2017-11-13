package org.joo.scorpius.test.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.trigger.handle.ExecutorHandlingStrategy;

public class Executor3ThreadsTriggerHandlingTest extends AbstractTriggerTest {
	
	public static void main(String[] args) {
		Executor3ThreadsTriggerHandlingTest testCase = new Executor3ThreadsTriggerHandlingTest(10000000);
		testCase.test();
	}
	
	private AtomicInteger processed = new AtomicInteger(0);
	
	private ExecutorHandlingStrategy strategy;
	
	public Executor3ThreadsTriggerHandlingTest(long iterations) {
		super(iterations);
		strategy = new ExecutorHandlingStrategy(7);
		manager.setHandlingStrategy(strategy);
	}

	@Override
	protected void doTest(String msgName) {
		processed = new AtomicInteger(0);
		CountDownLatch latch = new CountDownLatch(1);
		
		for(int i=0; i<iterations; i++) {
			manager.fire(msgName, new SampleRequest()).done(response -> {
				if (processed.incrementAndGet() == iterations) {
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
	protected void cleanup() {
		try {
			strategy.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
