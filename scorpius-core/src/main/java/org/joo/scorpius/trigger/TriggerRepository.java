package org.joo.scorpius.trigger;

import org.joo.scorpius.support.LifeCycle;

public interface TriggerRepository extends LifeCycle {

    public TriggerRegistration registerTrigger(final String name, final TriggerConfig triggerConfig);
    
    public TriggerConfig[] getTriggerConfigs(String name);
}
