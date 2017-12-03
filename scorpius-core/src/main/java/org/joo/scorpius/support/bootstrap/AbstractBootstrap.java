package org.joo.scorpius.support.bootstrap;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.Bootstrap;
import org.joo.scorpius.trigger.TriggerManager;

import lombok.Setter;

public abstract class AbstractBootstrap implements Bootstrap {

    protected @Setter TriggerManager triggerManager;
    
    protected @Setter ApplicationContext applicationContext;
    
    public static Bootstrap from(Runnable runnable) {
        return new AbstractBootstrap() {
            
            @Override
            public void run() {
                runnable.run();
            }
        };
    }
}
