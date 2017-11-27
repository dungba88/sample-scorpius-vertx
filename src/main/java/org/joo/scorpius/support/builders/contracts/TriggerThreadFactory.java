package org.joo.scorpius.support.builders.contracts;

import java.util.concurrent.ThreadFactory;

public interface TriggerThreadFactory extends ThreadFactory {

    public boolean isConsumerThread(Thread thread);
}
