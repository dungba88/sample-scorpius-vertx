package org.joo.scorpius.support.exception;

public class QueueInitializationException extends RuntimeException {

    private static final long serialVersionUID = -2068764964116066654L;

    public QueueInitializationException(Exception e) {
        super(e);
    }
}
