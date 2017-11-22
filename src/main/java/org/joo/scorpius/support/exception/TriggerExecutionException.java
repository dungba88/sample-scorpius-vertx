package org.joo.scorpius.support.exception;

public class TriggerExecutionException extends Exception {

	private static final long serialVersionUID = 5651707579614655859L;
	
	public TriggerExecutionException(String msg) {
		super(msg);
	}

	public TriggerExecutionException(Throwable cause) {
		super(cause);
	}

	public TriggerExecutionException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
