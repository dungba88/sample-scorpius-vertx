package org.joo.scorpius;

import org.joo.scorpius.trigger.TriggerManager;

public interface Bootstrap {

    public void setTriggerManager(TriggerManager triggerManager);

    public void setApplicationContext(ApplicationContext applicationContext);

    public void run();
}
