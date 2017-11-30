package org.joo.scorpius.test.perf;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.builders.ApplicationContextBuilder;
import org.joo.scorpius.support.builders.contracts.IdGenerator;
import org.joo.scorpius.support.builders.contracts.TriggerHandlingStrategyFactory;
import org.joo.scorpius.support.builders.id.AtomicIdGenerator;
import org.joo.scorpius.support.builders.id.TimeBasedIdGenerator;
import org.joo.scorpius.support.builders.id.UUIDGenerator;
import org.joo.scorpius.support.exception.MalformedRequestException;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.test.support.BrokenTrigger;
import org.joo.scorpius.test.support.RetryTrigger;
import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.test.support.TraceIdRequiredRequest;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.handle.disruptor.DisruptorHandlingStrategy;
import org.joo.scorpius.trigger.impl.DefaultTriggerManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

public class TestTriggerManager {

    private DefaultTriggerManager manager;

    private RetryTrigger retryTrigger = new RetryTrigger();

    public TestTriggerManager() {
        ApplicationContext context = new ApplicationContextBuilder().build();
        context.override(IdGenerator.class, new TimeBasedIdGenerator());
        context.override(TriggerHandlingStrategyFactory.class, () -> new DisruptorHandlingStrategy());
        this.manager = new DefaultTriggerManager(context);
        this.manager.registerTrigger("greet_java", new TriggerConfig(new SampleTrigger()))
                .withCondition(execContext -> execContext.getRequest() != null);
        this.manager.registerTrigger("greet_java", new TriggerConfig(new BrokenTrigger()))
                .withCondition(execContext -> execContext.getRequest() == null);
        this.manager.registerTrigger("retryable_consumer").withAction(retryTrigger);
        RetryPolicy retryPolicy = new RetryPolicy().retryOn(TriggerExecutionException.class)
                .withDelay(100, TimeUnit.MILLISECONDS).withMaxRetries(3);
        this.manager.registerTrigger("retryable_registration").withAction(retryTrigger)
                .withFailSafe(Failsafe.with(retryPolicy));
        try {
            this.manager.registerTrigger("greet_java_2").withCondition("request is not null")
                    .withAction(SampleTrigger.class);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDecodeNull() {
        try {
            manager.decodeRequestForEvent(null, null);
        } catch (MalformedRequestException ex) {
            Assert.assertEquals("Event name is null", ex.getMessage());
        }

        try {
            BaseRequest request = manager.decodeRequestForEvent("wrongEvent", null);
            Assert.assertNull(request);
        } catch (MalformedRequestException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testFireWrongEvent() {
        CountDownLatch latch = new CountDownLatch(2);
        manager.fire("wrongEvent", null, response -> {
            Assert.assertNull(response);
            latch.countDown();
        }, null);

        manager.fire("greet_java_2", null, response -> {
            Assert.assertNull(response);
            latch.countDown();
        }, null);

        try {
            latch.await();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testException() {
        CountDownLatch latch = new CountDownLatch(2);
        manager.fire("greet_java", null).fail(ex -> {
            Assert.assertTrue(ex.getCause() instanceof UnsupportedOperationException
                    && ex.getCause().getMessage().equals("broken"));
            latch.countDown();
        });

        manager.fire("greet_java", null, null, ex -> {
            Assert.assertTrue(ex.getCause() instanceof UnsupportedOperationException
                    && ex.getCause().getMessage().equals("broken"));
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAttachRequest() {
        SampleRequest request = new SampleRequest();

        Assert.assertNull(request.getTraceId());

        Optional<String> id = manager.getApplicationContext().getInstance(IdGenerator.class).create();
        request.attachTraceId(id);
        Assert.assertNotNull(id != null && id.isPresent());

        try {
            request.attachTraceId(Optional.empty());
        } catch (IllegalStateException ex) {
            Assert.assertEquals("TraceId is already attached", ex.getMessage());
        }

        Assert.assertEquals(id.orElse(null), request.getTraceId());
        Assert.assertTrue(request.verifyTraceId());

        TraceIdRequiredRequest requiredRequest = new TraceIdRequiredRequest();

        CountDownLatch latch = new CountDownLatch(1);
        manager.fire("greet_java", requiredRequest, null, ex -> {
            Assert.assertEquals("TraceId has not been attached", ex.getMessage());
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAtomicId() {
        testIdGenerator(new AtomicIdGenerator());
    }

    @Test
    public void testTimeBasedId() {
        testIdGenerator(new TimeBasedIdGenerator());
    }

    @Test
    public void testUUID() {
        testIdGenerator(new UUIDGenerator());
    }

    private void testIdGenerator(IdGenerator generator) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < 1000000; i++) {
            String id = generator.create().get();
            if (set.contains(id)) {
                Assert.fail("UUID collision detected");
                return;
            }
            set.add(id);
        }
    }

    @Test
    public void testRetryFromConsumer() {
        Assert.assertEquals(0, retryTrigger.getRetries());

        CountDownLatch latch = new CountDownLatch(1);
        RetryPolicy retryPolicy = new RetryPolicy().retryOn(TriggerExecutionException.class)
                .withDelay(100, TimeUnit.MILLISECONDS).withMaxRetries(6);
        manager.fire("retryable_consumer", new SampleRequest(), Failsafe.with(retryPolicy)).fail(ex -> {
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertEquals(7, retryTrigger.getRetries());
    }

    @Test
    public void testRetryFromOverridenRegistration() {
        Assert.assertEquals(0, retryTrigger.getRetries());

        CountDownLatch latch = new CountDownLatch(1);
        RetryPolicy retryPolicy = new RetryPolicy().retryOn(TriggerExecutionException.class)
                .withDelay(100, TimeUnit.MILLISECONDS).withMaxRetries(6);
        manager.fire("retryable_registration", new SampleRequest(), Failsafe.with(retryPolicy)).fail(ex -> {
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertEquals(7, retryTrigger.getRetries());
    }

    @Test
    public void testRetryFromRegistration() {
        Assert.assertEquals(0, retryTrigger.getRetries());

        CountDownLatch latch = new CountDownLatch(1);
        manager.fire("retryable_registration", new SampleRequest()).fail(ex -> {
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertEquals(4, retryTrigger.getRetries());
    }

    @After
    public void tearDown() {
        manager.shutdown();
    }
}
