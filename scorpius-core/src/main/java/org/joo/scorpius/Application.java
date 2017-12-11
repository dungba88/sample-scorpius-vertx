package org.joo.scorpius;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joo.promise4j.Promise;
import org.joo.scorpius.support.builders.ApplicationContextBuilder;
import org.joo.scorpius.support.builders.Builder;
import org.joo.scorpius.trigger.TriggerManager;
import org.joo.scorpius.trigger.impl.DefaultTriggerManager;

public class Application {

	private TriggerManager triggerManager;

	private AtomicBoolean initialized;

	private ApplicationContext applicationContext;

	private Bootstrap<?> bootstrap;

	public Application() {
		this(new ApplicationContextBuilder());
	}

	public Application(final Builder<ApplicationContext> applicationContextBuilder) {
		this.initialized = new AtomicBoolean(false);
		this.applicationContext = applicationContextBuilder.build();
	}

	public <T> Promise<T, Throwable> run(final Bootstrap<T> bootstrap) {
		if (!initialized.compareAndSet(false, true))
			throw new IllegalStateException("Application is already running");

		this.bootstrap = bootstrap;
		this.triggerManager = new DefaultTriggerManager(applicationContext);
		bootstrap.setTriggerManager(triggerManager);
		bootstrap.setApplicationContext(applicationContext);
		Promise<T, Throwable> promise = bootstrap.run();

		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
		return promise;
	}

	public void shutdown() {
		if (triggerManager != null)
			triggerManager.shutdown();
		if (bootstrap != null)
			bootstrap.shutdown();
	}
}
