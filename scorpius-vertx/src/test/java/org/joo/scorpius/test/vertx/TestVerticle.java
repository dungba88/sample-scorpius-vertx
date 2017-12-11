package org.joo.scorpius.test.vertx;

import java.util.concurrent.CountDownLatch;

import org.joo.scorpius.Application;
import org.joo.scorpius.Bootstrap;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.bootstrap.AbstractBootstrap;
import org.joo.scorpius.support.message.ExecutionContextMessage;
import org.joo.scorpius.support.vertx.eventbus.EventBusHandlingStrategy;
import org.joo.scorpius.support.vertx.verticle.SimpleScorpiusVerticle;
import org.joo.scorpius.test.support.SampleResponse;
import org.joo.scorpius.trigger.TriggerManager;
import org.junit.Assert;
import org.junit.Test;

import io.vertx.core.Vertx;

public class TestVerticle {

	private SimpleScorpiusVerticle verticle1 = new SimpleScorpiusVerticle().withBootstrapConfig(this::configBootstrap1);
	private SimpleScorpiusVerticle verticle2 = new SimpleScorpiusVerticle().withBootstrapConfig(this::configBootstrap2);

	@Test
	public void test() {
		Vertx vertx = Vertx.vertx();

		CountDownLatch latch = new CountDownLatch(2);

		vertx.deployVerticle(verticle1, res -> {
			if (res.failed())
				res.cause().printStackTrace();
			latch.countDown();
		});
		vertx.deployVerticle(verticle2, res -> {
			if (res.failed())
				res.cause().printStackTrace();
			latch.countDown();
		});

		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			Assert.fail(e.getMessage());
		}

		vertx.eventBus().<ExecutionContextMessage>consumer("scorpius").handler(event -> {
			verticle2.getApplication().getTriggerManager()
					.fire(event.body().getEventName(), (BaseRequest) event.body().getData()).done(res -> {
						ExecutionContextMessage message = new ExecutionContextMessage(event.body().getId(),
								event.body().getEventName(), res);
						event.reply(message);
					}).fail(ex -> {
						event.fail(1, ex.getMessage());
					});
		});

		CountDownLatch latch2 = new CountDownLatch(1);

		verticle1.getApplication().getTriggerManager().fire("greet_1", null, res -> {
			SampleResponse response = (SampleResponse) res;
			if (response.getName().equals("2"))
				latch2.countDown();
		}, null);

		try {
			latch2.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			Assert.fail(e.getMessage());
		}

		vertx.close();
	}

	private Bootstrap<?> configBootstrap1(Application application, Vertx vertx) {
		return AbstractBootstrap.from(() -> {
			TriggerManager manager = application.getTriggerManager();
			manager.setHandlingStrategy(new EventBusHandlingStrategy(vertx.eventBus()));
			manager.registerTrigger("greet_1");
		});
	}

	private Bootstrap<?> configBootstrap2(Application application, Vertx vertx) {
		return AbstractBootstrap.from(() -> {
			TriggerManager manager = application.getTriggerManager();
			manager.registerTrigger("greet_1").withAction(executionContext -> {
				manager.fire("greet_2", null, executionContext::finish, executionContext::fail);
			});
			manager.registerTrigger("greet_2").withAction(executionContext -> {
				executionContext.finish(new SampleResponse("2"));
			});
		});
	}
}
