package org.joo.scorpius;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joo.scorpius.support.builders.ApplicationContextBuilder;
import org.joo.scorpius.support.builders.Builder;
import org.joo.scorpius.trigger.TriggerManager;
import org.joo.scorpius.trigger.impl.DefaultTriggerManager;

public class Application {

    private TriggerManager triggerManager;

    private AtomicBoolean initialized;

    private ApplicationContext applicationContext;

    public Application() {
        this(new ApplicationContextBuilder());
    }

    public Application(final Builder<ApplicationContext> applicationContextBuilder) {
        this.initialized = new AtomicBoolean(false);
        this.applicationContext = applicationContextBuilder.build();
    }

    public void run(final Bootstrap bootstrap) {
        if (!initialized.compareAndSet(false, true))
            throw new IllegalStateException("Application is already running");

        this.triggerManager = new DefaultTriggerManager(applicationContext);
        bootstrap.setTriggerManager(triggerManager);
        bootstrap.setApplicationContext(applicationContext);
        bootstrap.run();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            
            @Override
            public void run() {
                triggerManager.shutdown();
            }
        });
    }
}
