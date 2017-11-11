package org.joo.scorpius.test.vertx;

import java.util.concurrent.Executors;

import org.joo.scorpius.support.deferred.AsyncDeferredObject;
import org.joo.scorpius.support.vertx.VertxBootstrap;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.trigger.handle.disruptor.DisruptorHandlingStrategy;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

import io.vertx.core.VertxOptions;

public class SampleVertxBootstrap extends VertxBootstrap {
	
	public void run() {
		configuredDeferredFactory();
		configureTriggers();
		
		VertxOptions options = new VertxOptions().setEventLoopPoolSize(8);
		configureServer(options);
	}

	private void configuredDeferredFactory() {
		applicationContext.setDeferredFactory(() -> new AsyncDeferredObject<>());
	}

	private void configureTriggers() {
		triggerManager.setHandlingStrategy(new DisruptorHandlingStrategy(1024, Executors.newFixedThreadPool(3), ProducerType.MULTI, new YieldingWaitStrategy(), true));
		triggerManager.registerTrigger("greet").withAction(new SampleTrigger());
	}
}
