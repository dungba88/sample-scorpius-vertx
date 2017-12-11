package org.joo.scorpius.test.unit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.joo.scorpius.Application;
import org.joo.scorpius.support.bootstrap.AbstractBootstrap;
import org.joo.scorpius.support.bootstrap.CompositionBootstrap;
import org.junit.Assert;
import org.junit.Test;

public class TestApplication {

	@Test
	public void test() {
		AtomicInteger counter = new AtomicInteger(0);
		CountDownLatch latch = new CountDownLatch(1);
		Application application = new Application();
		application.run(new CompositionBootstrap(AbstractBootstrap.from(this::createVoid),
				AbstractBootstrap.from(this::createTwo))).done(res -> {
					if (res.get(0) == null && res.get(1).equals(2)) {
						counter.incrementAndGet();
						latch.countDown();
					}
				});
		
		try {
			latch.await(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals(1, counter.get());
	}

	public void createVoid() {
		// do nothing
	}

	public int createTwo() {
		return 2;
	}
}
