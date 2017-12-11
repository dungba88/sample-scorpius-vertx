package org.joo.scorpius.support.message;

import java.io.Serializable;

import lombok.Getter;

public class ExecutionContextMessage implements Serializable {

    private static final long serialVersionUID = 4814185340706357939L;

    private final @Getter String id;

    private final @Getter String eventName;

    private final @Getter Serializable data;

    public ExecutionContextMessage(final String id, final String eventName, final Serializable request) {
        this.id = id;
        this.eventName = eventName;
        this.data = request;
    }
}
