package org.joo.scorpius.test.vertx;

import java.util.concurrent.CountDownLatch;

import org.joo.scorpius.Application;
import org.junit.Test;

public class TestServer {

	@Test
	public void test() {
		CountDownLatch latch = new CountDownLatch(1);
		Application app = new Application();
		app.run(new SampleVertxBootstrap()).fail(ex -> ex.printStackTrace()).always((status, res, done) -> {
			latch.countDown();
		});

		try {
			latch.await();
		} catch (InterruptedException e) {
		}

		System.out.println("Start testing HTTP");
		new TestClient(4, 10000).test();
		System.out.println("Finish testing HTTP");
		
		app.shutdown();
	}
}
