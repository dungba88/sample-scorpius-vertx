package org.joo.scorpius.support.exception;

public class NoMatchingRouteException extends Exception {

    private static final long serialVersionUID = -8387484821188812386L;

    public NoMatchingRouteException(final String msg) {
        super(msg);
    }
}
