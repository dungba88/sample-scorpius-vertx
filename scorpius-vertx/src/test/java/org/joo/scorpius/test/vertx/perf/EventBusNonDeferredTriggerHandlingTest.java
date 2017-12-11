package org.joo.scorpius.test.vertx.perf;

import java.util.concurrent.CountDownLatch;

import org.joo.scorpius.support.vertx.eventbus.EventBusHandlingStrategy;
import org.joo.scorpius.test.support.SampleRequest;

import io.vertx.core.Vertx;

public class EventBusNonDeferredTriggerHandlingTest extends AbstractTriggerTest {

    private long processed = 0;

    public EventBusNonDeferredTriggerHandlingTest(long iterations) {
        super(iterations, new EventBusHandlingStrategy(Vertx.vertx().eventBus(), true));
    }

    @Override
    protected void doTest(long iterations, String msgName) {
        processed = 0;
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < iterations; i++) {
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