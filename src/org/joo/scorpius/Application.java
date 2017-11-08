package org.joo.scorpius;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joo.scorpius.support.builders.ApplicationContextBuilder;
import org.joo.scorpius.support.builders.Builder;
import org.joo.scorpius.trigger.TriggerManager;

public class Application {

	private TriggerManager triggerManager;
	
	private AtomicBoolean initialized;
	
	private Builder<ApplicationContext> applicationContextBuilder;

	public Application() {
		this.initialized = new AtomicBoolean(false);
		this.applicationContextBuilder = new ApplicationContextBuilder(); 
	}

	public void run(Bootstrap bootstrap) {
		if (!initialized.compareAndSet(false, true))
			throw new RuntimeException("Application is already running");

		ApplicationContext applicationContext = applicationContextBuilder.build();
		this.triggerManager = new TriggerManager(applicationContext);
		bootstrap.setTriggerManager(triggerManager);
		bootstrap.setApplicationContext(applicationContext);
		bootstrap.run();
	}
}
