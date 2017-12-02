package org.joo.scorpius.support.exception;

public class TriggerExecutionException extends Exception {

    private static final long serialVersionUID = 5651707579614655859L;

    public TriggerExecutionException(final String msg) {
        super(msg);
    }

    public TriggerExecutionException(final Throwable cause) {
        super(cause);
    }

    public TriggerExecutionException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
