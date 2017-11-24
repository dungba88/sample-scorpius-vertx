package org.joo.scorpius.test.perf;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.builders.ApplicationContextBuilder;
import org.joo.scorpius.support.builders.contracts.IdGenerator;
import org.joo.scorpius.support.builders.contracts.TriggerHandlingStrategyFactory;
import org.joo.scorpius.support.builders.id.TimeBasedIdGenerator;
import org.joo.scorpius.support.exception.MalformedRequestException;
import org.joo.scorpius.test.support.BrokenTrigger;
import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.handle.disruptor.DisruptorHandlingStrategy;
import org.joo.scorpius.trigger.impl.DefaultTriggerManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class TestTriggerManager {
    
    private ApplicationContext context;

    private DefaultTriggerManager manager;

    public TestTriggerManager() {
        this.context = new ApplicationContextBuilder().build();
        this.context.override(IdGenerator.class, new TimeBasedIdGenerator());
        this.context.override(TriggerHandlingStrategyFactory.class, () -> new DisruptorHandlingStrategy());
        this.manager = new DefaultTriggerManager(context);
        this.manager.registerTrigger("greet_java", new TriggerConfig(new SampleTrigger())).withCondition(context -> context.getRequest() != null);
        this.manager.registerTrigger("greet_java", new TriggerConfig(new BrokenTrigger())).withCondition(context -> context.getRequest() == null);
        try {
            this.manager.registerTrigger("greet_java_2").withCondition("name is null").withAction(SampleTrigger.class);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDecodeNull() {
        try {
            manager.decodeRequestForEvent(null, null);
        } catch(MalformedRequestException ex) {
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
        CountDownLatch latch = new CountDownLatch(1);
        manager.fire("greet_java", null).fail(ex -> {
            Assert.assertTrue(ex.getCause() instanceof UnsupportedOperationException && ex.getCause().getMessage().equals("broken"));
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
    }

    @After
    public void tearDown() {
        manager.shutdown();
    }
}
