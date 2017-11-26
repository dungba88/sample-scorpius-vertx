package org.joo.scorpius.support.builders;

import java.util.concurrent.ThreadFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain=true)
public class DefaultThreadFactoryBuilder implements Builder<ThreadFactory> {
    
    private boolean daemon;
    
    private int priority = Thread.NORM_PRIORITY;
    
    @Override
    public ThreadFactory build() {
        return new ThreadFactory() {
            
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                if (t.isDaemon() != daemon)
                    t.setDaemon(daemon);
                if (t.getPriority() != priority)
                    t.setPriority(priority);
                return t;
            }
        };
    }
}
