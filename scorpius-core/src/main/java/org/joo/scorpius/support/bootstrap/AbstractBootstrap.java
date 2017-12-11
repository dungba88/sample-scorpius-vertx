package org.joo.scorpius.support.bootstrap;

import java.util.function.Supplier;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.SimpleDonePromise;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.Bootstrap;
import org.joo.scorpius.trigger.TriggerManager;

import lombok.Setter;

public abstract class AbstractBootstrap<T> implements Bootstrap<T> {

    protected @Setter TriggerManager triggerManager;
    
    protected @Setter ApplicationContext applicationContext;
    
    public static Bootstrap<Void> from(Runnable runnable) {
        return new AbstractBootstrap<Void>() {
            
            @Override
            public Promise<Void, Throwable> run() {
                runnable.run();
                return new SimpleDonePromise<>(null);
            }
        };
    }
    
    public static <T> Bootstrap<T> from(Supplier<T> supplier) {
        return new AbstractBootstrap<T>() {
            
            @Override
            public Promise<T, Throwable> run() {
                return new SimpleDonePromise<>(supplier.get());
            }
        };
    }
    
    public void shutdown() {
    		// do nothing
    }
}
