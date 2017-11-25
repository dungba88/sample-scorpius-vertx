package org.joo.scorpius.support.message;

import java.io.Serializable;

import org.joo.scorpius.support.BaseRequest;

public class PeriodicTaskMessage implements Serializable {

    private static final long serialVersionUID = 7708542234121823317L;

    private final long delay;

    private final long period;

    private final BaseRequest request;

    public PeriodicTaskMessage(final long delay, final long period, final BaseRequest request) {
        this.delay = delay;
        this.period = period;
        this.request = request;
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    public BaseRequest getRequest() {
        return request;
    }
}
