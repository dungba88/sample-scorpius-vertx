package org.joo.scorpius.support.builders;

import java.util.concurrent.atomic.AtomicInteger;

import org.joo.scorpius.support.builders.contracts.TriggerThreadFactory;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class DefaultThreadFactoryBuilder implements Builder<TriggerThreadFactory> {

    private @Getter boolean daemon = false;

    private @Getter int priority = Thread.NORM_PRIORITY;

    private @Getter String prefix = "trigger-worker-";

    private AtomicInteger counter = new AtomicInteger(0);

    public DefaultThreadFactoryBuilder() {

    }

    public DefaultThreadFactoryBuilder(final int priority, final boolean daemon, final @NonNull String prefix) {
        this.priority = priority;
        this.daemon = daemon;
        this.prefix = prefix;
    }

    @Override
    public TriggerThreadFactory build() {
        return new TriggerThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, prefix + counter.getAndIncrement());
                if (t.isDaemon() != daemon)
                    t.setDaemon(daemon);
                if (t.getPriority() != priority)
                    t.setPriority(priority);
                return t;
            }

            @Override
            public boolean isConsumerThread(Thread thread) {
                return thread.getName().startsWith(prefix);
            }
        };
    }
}
