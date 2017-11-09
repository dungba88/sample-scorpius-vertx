package org.joo.scorpius.test.vertx;

import org.joo.scorpius.support.deferred.AsyncDeferredObject;
import org.joo.scorpius.support.vertx.VertxBootstrap;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.handle.DefaultHandlingStrategy;

public class SampleVertxBootstrap extends VertxBootstrap {
	
	public void run() {
		configuredDeferredFactory();
		configureTriggers();
		configureServer();
	}

	private void configuredDeferredFactory() {
		applicationContext.setDeferredFactory(() -> new AsyncDeferredObject<>());
	}

	private void configureTriggers() {
		triggerManager.setHandlingStrategy(new DefaultHandlingStrategy());
		triggerManager.registerTrigger("greet", new TriggerConfig(new SampleTrigger()));
	}
}
