package org.joo.scorpius.support.exception;

public class MalformedRequestException extends Exception {

    private static final long serialVersionUID = 5651707579614655859L;

    public MalformedRequestException(final String msg) {
        super(msg);
    }

    public MalformedRequestException(final Throwable cause) {
        super(cause);
    }

    public MalformedRequestException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
