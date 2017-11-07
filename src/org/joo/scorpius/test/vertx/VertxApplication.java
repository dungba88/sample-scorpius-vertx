package org.joo.scorpius.test.vertx;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joo.scorpius.test.vertx.trigger.TriggerManager;

import io.vertx.core.Vertx;

public class VertxApplication {

	private Vertx vertx;
	
	private TriggerManager triggerManager;
	
	private AtomicBoolean initialized;

	public VertxApplication() {
		this.vertx = Vertx.factory.vertx();
		this.initialized = new AtomicBoolean(false);
	}

	public void run(Bootstrap bootstrap) {
		if (!initialized.compareAndSet(false, true))
			throw new RuntimeException("Application is already running");

		VertxApplicationContext applicationContext = new VertxApplicationContext(vertx);
		this.triggerManager = new TriggerManager(applicationContext);
		bootstrap.setTriggerManager(triggerManager);
		bootstrap.setApplicationContext(applicationContext);
		bootstrap.run();
	}
}
