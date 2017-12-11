package org.joo.scorpius.test.vertx.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.joo.scorpius.support.message.ExecutionContextMessage;
import org.joo.scorpius.support.vertx.eventbus.EventBusHandlingStrategy;
import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.test.support.SampleResponse;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

public class EventBusNonDeferredTriggerHandlingTest extends AbstractTriggerTest {

	private static EventBus eventBus = Vertx.vertx().eventBus();

	static {
		eventBus.<ExecutionContextMessage>consumer("scorpius", EventBusNonDeferredTriggerHandlingTest::onEvent);
	}

	private static final void onEvent(Message<ExecutionContextMessage> msg) {
		ExecutionContextMessage requestMessage = msg.body();
		SampleRequest request = (SampleRequest) requestMessage.getData();
		ExecutionContextMessage responseMessage = new ExecutionContextMessage(requestMessage.getId(),
				requestMessage.getEventName(), new SampleResponse("Hi " + request.getName()));
		msg.reply(responseMessage);
	}

	public EventBusNonDeferredTriggerHandlingTest(long iterations) {
		super(iterations, new EventBusHandlingStrategy(eventBus));
	}

	@Override
	protected void doTest(long iterations, String msgName) {
		AtomicInteger counter = new AtomicInteger();
		CountDownLatch latch = new CountDownLatch(1);

		for (int i = 0; i < iterations; i++) {
			manager.fire(msgName, new SampleRequest("name"), response -> {
				SampleResponse sampleResponse = (SampleResponse) response;
				if (sampleResponse.getName().equals("Hi name")) {
					if (counter.incrementAndGet() == iterations) {
						latch.countDown();
					}
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