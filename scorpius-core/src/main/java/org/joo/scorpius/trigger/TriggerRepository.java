package org.joo.scorpius.trigger;

import java.util.List;

import org.joo.scorpius.support.LifeCycle;

public interface TriggerRepository extends LifeCycle {

    public TriggerRegistration registerTrigger(final String name, final TriggerConfig triggerConfig);
    
    public List<TriggerConfig> getTriggerConfigs(String name);
}
