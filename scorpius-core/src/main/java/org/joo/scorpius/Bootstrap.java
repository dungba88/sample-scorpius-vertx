package org.joo.scorpius;

import org.joo.promise4j.Promise;
import org.joo.scorpius.trigger.TriggerManager;

public interface Bootstrap<T> {

    public void setTriggerManager(final TriggerManager triggerManager);

    public void setApplicationContext(final ApplicationContext applicationContext);

    public Promise<T, Throwable> run();
    
    public void shutdown();
}
