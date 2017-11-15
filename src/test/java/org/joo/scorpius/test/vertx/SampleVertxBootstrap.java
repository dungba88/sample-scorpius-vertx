package org.joo.scorpius.test.vertx;

import java.util.concurrent.Executors;

import org.joo.scorpius.support.vertx.VertxBootstrap;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.trigger.handle.disruptor.DisruptorHandlingStrategy;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

import io.vertx.core.VertxOptions;

public class SampleVertxBootstrap extends VertxBootstrap {
	
	public void run() {
		configureTriggers();
		
		VertxOptions options = new VertxOptions().setEventLoopPoolSize(8);
		configureServer(options, 8080);
	}

	private void configureTriggers() {
		triggerManager.setHandlingStrategy(new DisruptorHandlingStrategy(1024, Executors.newFixedThreadPool(3), ProducerType.MULTI, new YieldingWaitStrategy()));
		triggerManager.registerTrigger("greet_java").withAction(new SampleTrigger());
	}
}
