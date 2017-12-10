package org.joo.scorpius;

import org.joo.scorpius.trigger.TriggerManager;

public interface Bootstrap {

    public void setTriggerManager(final TriggerManager triggerManager);

    public void setApplicationContext(final ApplicationContext applicationContext);

    public void run();
    
    public void shutdown();
}
