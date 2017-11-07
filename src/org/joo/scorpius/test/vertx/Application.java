package org.joo.scorpius.test.vertx;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joo.scorpius.test.vertx.trigger.TriggerManager;

public class Application {

	private TriggerManager triggerManager;
	
	private AtomicBoolean initialized;

	public Application() {
		this.initialized = new AtomicBoolean(false);
	}

	public void run(Bootstrap bootstrap) {
		if (!initialized.compareAndSet(false, true))
			throw new RuntimeException("Application is already running");

		ApplicationContext applicationContext = new ApplicationContext();
		this.triggerManager = new TriggerManager(applicationContext);
		bootstrap.setTriggerManager(triggerManager);
		bootstrap.setApplicationContext(applicationContext);
		bootstrap.run();
	}
}
